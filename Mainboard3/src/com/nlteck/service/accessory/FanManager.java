package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.FanInfo;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.AnotherFanStateQueryData;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;
import com.nltecklib.protocol.li.main.DeviceStateQueryData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;

/**
 * 风机管理器抽象基类
 * 
 * @author Administrator
 *
 */
public abstract class FanManager {

	protected MainBoard mainBoard;

	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	protected int fanSpeed = 0; // 散热风机默认速度
	protected List<Fan> coolFans = new ArrayList<Fan>();
	protected List<Fan> turboFans = new ArrayList<Fan>();

	protected List<FanSwitchListener> listeners = new ArrayList<FanSwitchListener>();

	private final static double FAN_START_TEMP_UPPER = 40; // 智能风机启动温度上限
	private final static int LIST_CACHE = 40; // 缓存上限

	protected boolean commErr;

	protected boolean use;
	protected boolean monitor;
	protected boolean alert; // 有一半风机发生故障
	protected int commCount; // 通信故障

	private List<Double> tempList = new LinkedList<Double>(); // 温度检测缓存

	/**
	 * 风机开关监听器
	 * 
	 * @author Administrator
	 *
	 */
	public interface FanSwitchListener {

		/**
		 * 风机启动和停止监听
		 * 
		 * @param open
		 */
		public void power(boolean open);
	}

	/**
	 * 监视风机状态
	 * 
	 * @author wavy_zheng 2020年12月23日
	 * @throws AlertException
	 */
	public void monitorFans() throws AlertException {

		if (mainBoard.isPoweroff()) {

			return;
		}
		try {
			if (MainBoard.startupCfg.getFanManagerInfo().monitor) {

				if (MainBoard.startupCfg.getProtocol().useAnotherFanStateQuery) {

					List<AnotherFanStateQueryData> list = new ArrayList<AnotherFanStateQueryData>();

					for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

						int index = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n).getIndex();
						if (MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n).fanType == FanType.COOL) {
							AnotherFanStateQueryData afsqd = readAnotherFansState(index);
							if (afsqd != null) {
								list.add(afsqd);
							}

						}
					}
					decodeAnotherFanStateQuerys(list);

				} else {
					FanStateQueryData fsqd = readFansState(0);
					// 解析采集到的风机数据
					decodeFanState(fsqd);
				}
			}

			int faultCount = 0;

			if (!alert) {

				StringBuffer info = new StringBuffer("散热风机");

				for (int n = 0; n < getHeatFanCount(); n++) {

					if (getHeatFanByIndex(n).getWs() == WorkState.FAULT) {

						info.append(n + 1 + ",");
						faultCount++;
					}

				}
				if (faultCount > 0) {
					alert = true;
					info.append("发生故障!");

					throw new AlertException(AlertCode.FAN, info.toString());
				}

				for (int n = 0; n < getTurboFanCount(); n++) {

					if (getTurboFanByIndex(n).getWs() == WorkState.FAULT) {

						alert = true;
						if (n < 2) {

							throw new AlertException(AlertCode.FAN, "轴流风机" + (n + 1) + "故障");
						} else {
							throw new AlertException(AlertCode.FAN, "涡轮风机" + (n + 1) + "故障");
						}
					}
				}

			} else {

				for (int n = 0; n < getHeatFanCount(); n++) {

					if (getHeatFanByIndex(n).getWs() == WorkState.FAULT) {

						faultCount++;
					}

				}
				if (faultCount >= getHeatFanCount() / 2 && mainBoard.getAlertCode() != AlertCode.DEVICE_ERROR
						&& mainBoard.getState() == State.FORMATION) {

					CommonUtil.sleep(6000); // 延时关闭,避免在刚启动就切断逆变
					throw new AlertException(AlertCode.DEVICE_ERROR, "风机故障数过多,切断电源并暂停流程");
				}
				int turboFanFault = 0;
				for (int n = 0; n < getTurboFanCount(); n++) {

					if (getTurboFanByIndex(n).getWs() == WorkState.FAULT) {

						turboFanFault++;
					}
				}

				if (faultCount == 0 && turboFanFault == 0) {

					alert = false;
				}

			}

			// 恢复
			if (commCount > 0) {

				Context.getAlertManager().handle(AlertCode.COMM_ERROR, "", true);
				commCount = 0;
			}

		} catch (AlertException ex) {

			if (ex.getAlertCode() == AlertCode.COMM_ERROR) {

				commCount++;
				if (commCount < 3) {

					return;
				}

			}
			if (commCount < 5) {
				try {
					Context.getAlertManager().handle(ex.getAlertCode(), ex.getMessage(),
							ex.getMessage().contains("恢复正常") ? true : false);
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public FanManager(MainBoard mainBoard) throws AlertException {

		this.mainBoard = mainBoard;
		use = MainBoard.startupCfg.getFanManagerInfo().use;
		monitor = MainBoard.startupCfg.getFanManagerInfo().monitor;

		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo fi = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);
			if (fi.fanType != FanType.TURBO) {

				for (int i = 0; i < fi.fanCount; i++) {

					Fan fan = new Fan(i, fi.fanType);

					coolFans.add(fan);

				}

			} else {

				for (int i = 0; i < fi.fanCount; i++) {

					turboFans.add(new Fan(i, fi.fanType));
				}
			}
		}

	}

	/**
	 * 操控风机开关
	 * 
	 * @param index
	 *            暂时不支持，使用0xff
	 * @param direction
	 *            IN OUT 风机进出风口，暂时不支持该功能
	 * @param state
	 *            on开启风机，off关闭风机
	 * @param grade
	 *            风速等级，暂时不起作用
	 * @return
	 * @throws AlertException
	 */
	public abstract boolean fan(int index, Direction direction, PowerState state, int grade) throws AlertException;

	/**
	 * 读取监控风机状态
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract FanStateQueryData readFansState(int index) throws AlertException;

	/**
	 * 读取风机监控状态
	 * 
	 * @author wavy_zheng 2020年3月9日
	 * @param index
	 * @return
	 * @throws AlertException
	 */
	public abstract AnotherFanStateQueryData readAnotherFansState(int index) throws AlertException;

	/**
	 * 获取风机对象
	 * 
	 * @param index
	 * @return
	 */
	public Fan getHeatFanByIndex(int index) {

		return coolFans.get(index);
	}

	public Fan getTurboFanByIndex(int index) {

		return turboFans.get(index);
	}

	/**
	 * 将获取到的风机状态刷新到各个对象中
	 * 
	 * @param fsqd
	 */
	protected void decodeFanState(FanStateQueryData fsqd) {

		for (int n = 0; n < fsqd.getHeatFanCount(); n++) {

			if (n == 0 && MainBoard.startupCfg.isUseVirtualData()) {

				getHeatFanByIndex(n).setPs(PowerState.OFF);
				getHeatFanByIndex(n).setWs(WorkState.FAULT);
			} else {

				getHeatFanByIndex(n)
						.setPs((fsqd.getHeatFanSwitchFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON
								: PowerState.OFF);
				getHeatFanByIndex(n)
						.setWs((fsqd.getHeatFanStateFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT
								: WorkState.NORMAL);
			}

		}
		// System.out.println("fsqd.getTurboFanCount() = " + fsqd.getTurboFanCount());
		if (turboFans.size() > 0) {

			for (int n = 0; n < fsqd.getTurboFanCount(); n++) {

				getTurboFanByIndex(n)
						.setPs((fsqd.getTurboFanSwitchFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON
								: PowerState.OFF);
				getTurboFanByIndex(n)
						.setWs((fsqd.getTurboFanStateFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT
								: WorkState.NORMAL);
			}
		}

		// System.out.println(coolFans);

	}

	public int getHeatFanCount() {

		return coolFans.size();
	}

	public int getTurboFanCount() {

		return turboFans.size();
	}

	/**
	 * 涡轮风机开关
	 * 
	 * @param powerState
	 * @throws AlertException
	 */
	public abstract void powerTurboFan(PowerState powerState) throws AlertException;

	/**
	 * 流程启动前系统检查所有风机状态
	 * 
	 * @throws AlertException
	 */
	public void checkFans() throws AlertException {

		// 检测涡轮风机是否有故障
		for (int n = 0; n < coolFans.size(); n++) {

			Fan fan = coolFans.get(n);
			if (fan.getType() == FanType.TURBO && fan.getWs() == WorkState.FAULT) {

				throw new AlertException(AlertCode.FAN, "涡轮风机故障!");
			}
		}

		canStartup();
	}

	/**
	 * 智能风机待机检测
	 * 
	 * @throws AlertException
	 */
	private void checkWait() throws AlertException {

		if (mainBoard.getState() == State.FORMATION || mainBoard.getState() == State.CAL) {

			return;
		}
		if (!mainBoard.getEnergySaveData().isUseSmartFan()) {

			return; // 没有打开智能风机
		}

		// 检测当前驱动板的温度
		double maxDriverTemp = 0;

		for (DriverBoard db : mainBoard.listDrivers()) {

			if (db.getTemp1() > maxDriverTemp) {

				maxDriverTemp = db.getTemp1();
			}
			if (db.getTemp2() > maxDriverTemp) {

				maxDriverTemp = db.getTemp2();
			}
		}

		tempList.add(maxDriverTemp);
		if (tempList.size() > LIST_CACHE) {

			tempList.remove(0);
		}
		if (maxDriverTemp >= FAN_START_TEMP_UPPER && !isAnyCoolFanWork()) {

			// 启动风机
			fan(0, Direction.IN, PowerState.ON, 2);
			mainBoard.pushSendQueue(0xff, -1, AlertCode.NORMAL,
					"检测到驱动板温度" + CommonUtil.formatNumber(maxDriverTemp, 1) + "℃开启风机降温");
			tempList.clear(); // 清空缓存保证风机时长
		} else {

			if (maxDriverTemp < FAN_START_TEMP_UPPER && isAnyCoolFanWork() && isDriverCoolSteady()) {

				// 温控板温度稳定，开闭风机
				fan(0, Direction.IN, PowerState.OFF, 2);
				mainBoard.pushSendQueue(0xff, -1, AlertCode.NORMAL,
						"检测到驱动板温度" + CommonUtil.formatNumber(maxDriverTemp, 1) + "℃稳定关闭风机");

			}

		}

	}

	/**
	 * 驱动板温度在风机的作用下是否趋于稳定
	 * 
	 * @return
	 */
	private boolean isDriverCoolSteady() {

		if (tempList.size() < LIST_CACHE) {

			return false;
		}
		double lastTemp = tempList.get(tempList.size() - 1);
		for (int n = tempList.size() - 2; n >= 0; n--) {

			if (lastTemp < tempList.get(n)) {

				return false; // 正在降温
			}
		}

		return true;
	}

	/**
	 * 清空风机温度检测缓存
	 */
	public synchronized void clearTempCache() {

		tempList.clear();
	}

	public boolean isAnyCoolFanWork() {

		for (int n = 0; n < coolFans.size(); n++) {

			Fan fan = coolFans.get(n);
			if (fan.getType() != FanType.TURBO && fan.getPs() == PowerState.ON) {

				return true;
			}
		}

		return false;
	}

	public boolean isAnyTurboFanWork() {

		for (int n = coolFans.size() - 1; n >= 0; n--) {

			Fan fan = coolFans.get(n);
			if (fan.getType() == FanType.TURBO && fan.getPs() == PowerState.ON) {

				return true;
			}
		}

		return false;
	}

	public void addListener(FanSwitchListener listener) {

		listeners.add(listener);
	}

	public void removeListener(FanSwitchListener listener) {

		listeners.remove(listener);
	}

	public void triggerListener(boolean fanOpen) {

		for (FanSwitchListener listener : listeners) {

			listener.power(fanOpen);
		}
	}

	/**
	 * 整合多组风机状态
	 * 
	 * @author wavy_zheng 2020年3月9日
	 * @param fanStates
	 * @return
	 */
	protected void decodeAnotherFanStateQuerys(List<AnotherFanStateQueryData> fanStates) {

		int index = 0;
		for (AnotherFanStateQueryData fsqd : fanStates) {
			for (int n = 0; n < fsqd.getHeatFanCount(); n++) {

				getHeatFanByIndex(index)
						.setPs((fsqd.getHeatFanSwitchFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON
								: PowerState.OFF);
				getHeatFanByIndex(index)
						.setWs((fsqd.getHeatFanStateFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT
								: WorkState.NORMAL);
				index++;

			}
		}
		index = 0;
		// 解析涡轮风机
		for (AnotherFanStateQueryData fsqd : fanStates) {
			for (int n = 0; n < fsqd.getTurboFanCount(); n++) {

				getTurboFanByIndex(index)
						.setPs((fsqd.getTurboFanSwitchFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON
								: PowerState.OFF);
				getTurboFanByIndex(index)
						.setWs((fsqd.getTurboFanStateFlag().get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT
								: WorkState.NORMAL);

				index++;
			}
		}

	}

	/**
	 * 关闭非必要的风机组
	 * 
	 * @author wavy_zheng 2020年4月21日
	 * @throws AlertException
	 */
	public void shutdownCoolFanOther() throws AlertException {

		/**
		 * 关闭辅助风机
		 */
		for (int n = 1; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo fi = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);
			if (fi.use && fi.fanType == FanType.COOL) {
				fan(fi.index, Direction.IN, PowerState.OFF, 0);
				System.out.println("close fan group : " + fi.index);
			}

		}

	}

	/**
	 * 启动所有组风机
	 * 
	 * @author wavy_zheng 2020年4月21日
	 * @throws AlertException
	 */
	public void openAllCoolFans() throws AlertException {

		for (int n = 0; n < MainBoard.startupCfg.getFanManagerInfo().fanInfos.size(); n++) {

			FanInfo fi = MainBoard.startupCfg.getFanManagerInfo().fanInfos.get(n);
			if (fi.use && fi.fanType == FanType.COOL) {
				fan(fi.index, Direction.IN, PowerState.ON, 0);

				System.out.println("open fan group : " + fi.index);
			}
		}
	}

	public int getFanSpeed() {
		return fanSpeed;
	}

	public boolean isUse() {
		return use;
	}

	public void flushAllFansRunMiliseconds(long miliseconds) {

		for (Fan fan : coolFans) {

			if (fan.getPs() == PowerState.ON) {
				fan.setRunMiliseconds(fan.getRunMiliseconds() + miliseconds);
			}
		}

		for (Fan fan : turboFans) {

			if (fan.getPs() == PowerState.ON) {
				fan.setRunMiliseconds(fan.getRunMiliseconds() + miliseconds);
			}
		}

	}

	public boolean isMonitor() {
		return monitor;
	}

	public boolean isAlert() {
		return alert;
	}

	/**
	 * 能否启动风机
	 * 
	 * @author wavy_zheng 2021年6月3日
	 * @return
	 */
	public void canStartup() throws AlertException {

		int faultCount = 0;
		for (int n = 0; n < getHeatFanCount(); n++) {

			if (getHeatFanByIndex(n).getWs() == WorkState.FAULT) {

				faultCount++;
			}

		}
		if (faultCount > 3) {

			throw new AlertException(AlertCode.FAN, "当前设备超过3个风机故障，无法启动!");
		}

		/*
		 * faultCount = 0; for(int n = 0 ; n < getTurboFanCount() ; n++) {
		 * 
		 * if(getTurboFanByIndex(n).getWs() == WorkState.FAULT) {
		 * 
		 * faultCount++; } } if(faultCount >= getTurboFanCount() / 2) {
		 * 
		 * throw new AlertException(AlertCode.FAN,"当前设备超过半数涡轮风机故障，无法启动!"); }
		 */

	}

}
