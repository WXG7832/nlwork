package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.MainBoard;
import com.nlteck.firmware.PowerController;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.PowerInfo;
import com.nlteck.service.StartupCfgManager.PowerProduct;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.PowerErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData.CommState;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData2;
import com.nltecklib.protocol.li.main.DeviceStateQueryData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.utils.LogUtil;
import com.rm5248.serial.SerialPort;

/**
 * 逆变电源管理器
 * 
 * @author Administrator
 *
 */
public abstract class PowerManager {

	public final static int MIN_POWER_COUNT = 2;

	protected MainBoard mainBoard;
	protected PowerInfo chargePowerInfo;
	protected PowerInfo auxiliaryPowerInfo;
	protected List<PowerGroup> groups = new ArrayList<PowerGroup>();
	protected List<AuxiliaryPower> auxiliaryPowers = new ArrayList<AuxiliaryPower>();
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	protected long waitSeconds; // 设备待机时长min
	protected boolean emergencyPoweroff; // 设备紧急掉电
	protected int commCount; // 通信故障
	protected boolean use;
	protected boolean monitor; // 是否监控
	protected boolean alert; // 已发生报警
	protected PowerProduct product; // 电源产家

	protected int recovertyCount;
	protected boolean powerWork = true; // 电源工作模式,默认在工作模式
	protected Logger logger;

	protected List<PowerFactor> powerFactories = new ArrayList<>();

	private static class PowerFactor {

		public PowerFactor(double current, double factor) {

			this.current = current;
			this.factor = factor;
		}

		public double current;
		public double factor;
	}

	public PowerManager(MainBoard mainBoard) throws AlertException {

		this.mainBoard = mainBoard;
		use = MainBoard.startupCfg.getPowerManagerInfo().use;
		monitor = MainBoard.startupCfg.getPowerManagerInfo().monitor;
		product = MainBoard.startupCfg.getPowerManagerInfo().product;
		int index = MainBoard.startupCfg.getPowerManagerInfo().powerInfos
				.indexOf(new PowerInfo(PowerType.CHARGE, true, null));
		if (index != -1) {

			chargePowerInfo = MainBoard.startupCfg.getPowerManagerInfo().powerInfos.get(index);
		}

		index = MainBoard.startupCfg.getPowerManagerInfo().powerInfos
				.indexOf(new PowerInfo(PowerType.AUXILIARY, true, null));
		if (index != -1) {

			auxiliaryPowerInfo = MainBoard.startupCfg.getPowerManagerInfo().powerInfos.get(index);
		}

		logger = LogUtil.getLogger("powerSupply");

		// 电源系数
		powerFactories.add(new PowerFactor(3500.0, 0.013));
		powerFactories.add(new PowerFactor(4500.0, 0.026));
		powerFactories.add(new PowerFactor(12000.0, 0.062));

	}

	private double getPowerFactor(double maxCurrent) {

		for (int n = 0; n < powerFactories.size(); n++) {

			PowerFactor factor = powerFactories.get(n);

			if (maxCurrent >= factor.current
					&& (n == powerFactories.size() - 1 || maxCurrent < powerFactories.get(n + 1).current)) {

				return factor.factor;
			}
		}

		return 0.062; // 默认

	}

	/**
	 * 监控电源状态
	 * 
	 * @author wavy_zheng 2020年12月12日
	 * @throws AlertException
	 */
	public void monitorPowers() {

		if (mainBoard.isPoweroff()) {

			return;
		}

		try {

			if (chargePowerInfo.powerCount <= 16) {

				PowerStateQueryData psqd = readPowersState();
				// 解析成对象
				decodePowerQueryData(psqd);

			} else {

				PowerStateQueryData2 psqd = readPowersState2();
				// 解析成对象
				decodePowerQueryData2(psqd);
			}

			if (product == PowerProduct.GD) {
				// 检测电源板
				try {
					if (mainBoard.getState() == State.FORMATION) {

						if (!powerWork) {

							powerWork = true;
							writePowerSupplyState(powerWork);

						}

					} else {

						if (powerWork) {

							powerWork = false;
							writePowerSupplyState(powerWork);

						}

					}

				} catch (Exception ex) {

					/* 有可能控制板没有增加,屏蔽 */
					ex.printStackTrace();
					logger.error("write power supply error:" + CommonUtil.getThrowableException(ex));
				}

			}

			// 逆变电源待机检测
			checkWaitCondition(5);

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

	/**
	 * 逆变电源进入待机状态，因考虑风机供电，需至少保留1个电源供电
	 * 
	 * @author wavy_zheng 2021年4月1日
	 * @return
	 * @throws AlertException
	 */
	public void enterWaitState() throws AlertException {

		boolean alreadyOpenOne = false;
		for (int n = 0; n < groups.size(); n++) {

			for (int i = 0; i < groups.get(n).getPowerCount(); i++) {
				InverterPower power = groups.get(n).getPowerByIndex(i);
				if (power.getWs() == WorkState.NORMAL && power.getPs() == PowerState.ON) {

					if (alreadyOpenOne) {
						power.power(PowerState.OFF);
					} else {

						alreadyOpenOne = true;
					}

				}
			}
		}

	}

	/**
	 * 
	 * @param index
	 *            操作总电源
	 * @param ps
	 * @return
	 */
	public abstract boolean power(PowerState ps) throws AlertException;

	/**
	 * 获取总电源开关
	 * 
	 * @return
	 */
	public abstract PowerState getPowerSwitchState() throws AlertException;

	/**
	 * 读取电源监控状态数据
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract PowerStateQueryData readPowersState() throws AlertException;

	/**
	 * 读取图伟逆变电源的故障信息
	 * 
	 * @author wavy_zheng 2022年2月21日
	 * @return
	 * @throws AlertException
	 */
	public abstract PowerErrorInfoData readTBMPowerFaultInfos() throws AlertException;

	/**
	 * 读取电源监控状态数据2
	 * 
	 * @author wavy_zheng 2021年4月24日
	 * @return
	 * @throws AlertException
	 */
	public abstract PowerStateQueryData2 readPowersState2() throws AlertException;

	/**
	 * 通知电源控制板是否进入工作模式or待测模式 待测模式控制板将对电源进行故障自我修复 默认在工作模式
	 * 
	 * @author wavy_zheng 2021年9月25日
	 * @param work
	 * @throws AlertException
	 */
	public abstract void writePowerSupplyState(boolean work) throws AlertException;

	/**
	 * 读取电源故障信息
	 * 
	 * @author wavy_zheng 2020年12月14日
	 * @param powerIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract PowerFaultReasonData readPowerFaultInfo(int powerIndex) throws AlertException;

	/**
	 * 根据流程获取需要正常工作最小的逆变电源数
	 * 
	 * @param pd
	 * @return
	 */
	public int getMinNeedPowerCount(ProcedureData pd) {

		// 流程中出现的最大电流
		double maxStepCurrent = 0;

		for (int n = 0; n < pd.getStepCount(); n++) {

			Step step = pd.getStep(n);

			if (maxStepCurrent < step.specialCurrent) {

				maxStepCurrent = step.specialCurrent;
			}

		}

		double maxProcedureCurrent = maxStepCurrent;
		// 设备支持最大的电流
		double maxCurrent = MainBoard.startupCfg.getMaxDeviceCurrent();
		Environment.infoLogger.info("maxProcedureCurrent = " + maxProcedureCurrent + ",maxCurrent = " + maxCurrent);
		// 有效通道数
		int validChannelCount = MainBoard.startupCfg.getDeviceChnCount();

		Environment.infoLogger.info("validChannelCount = " + validChannelCount);
		Environment.infoLogger.info(maxProcedureCurrent + " / " + maxCurrent + " * "
				+ getPowerFactor(MainBoard.startupCfg.getMaxDeviceCurrent()) + "" + validChannelCount + "1");
		// 计算最小所需电源个数
		int needPowerCount = CommonUtil.getIntegerFromVal((double) maxProcedureCurrent / maxCurrent
				* getPowerFactor(MainBoard.startupCfg.getMaxDeviceCurrent()) * validChannelCount + 1);

		if (mainBoard.startupCfg.getMaxDeviceCurrent() > 15000) {

			needPowerCount += 4;
		}

		if (needPowerCount < MIN_POWER_COUNT) {

			needPowerCount = MIN_POWER_COUNT;
		}

		if (needPowerCount > getChargePowerCount()) {

			needPowerCount = getChargePowerCount();
		}

		return needPowerCount;

	}

	/**
	 * 最大限度开启电源，以满足最小需求
	 * 
	 * @return
	 * @throws AlertException
	 */
	protected boolean makePowerEnough(int minNeedPowerCount) throws AlertException {

		int validCount = 0;
		for (PowerGroup pg : groups) {

			pg.setData(0); // 复位
			Environment.infoLogger.info("power group " + pg.groupIndex + " : " + pg.getPowerCount());
		}
		do {
			boolean appendPower = false;
			// 每组轮番打开1个电源，直到满足数量
			for (PowerGroup pg : groups) {

				int index = (int) pg.getData();
				if (index < pg.getPowerCount()) {

					InverterPower power = pg.getPowerByIndex(index);
					if (power.getWs() == WorkState.NORMAL) {

						validCount++;
						if (power.getPs() == PowerState.OFF) {

							power.power(PowerState.ON);
						}
					} else {
						// 电源故障时关闭
						if (power.getPs() == PowerState.ON) {

							power.power(PowerState.OFF);
						}
					}
					pg.setData(++index);
					appendPower = true;

					if (validCount >= minNeedPowerCount) {

						break;
					}
				}
			}
			if (!appendPower) {

				return false;
				// throw new AlertException(AlertCode.POWER_DOWN,
				// I18N.getVal(I18N.PowerNotMeetProcedure, validCount));
			}
		} while (validCount < minNeedPowerCount);

		// 关闭多余电源
		for (PowerGroup pg : groups) {

			int index = (int) pg.getData();
			Environment.infoLogger.info("power group " + pg.groupIndex + "close redunt powers from index " + index);
			for (int n = index; n < pg.getPowerCount(); n++) {

				InverterPower power = pg.getPowerByIndex(n);
				Environment.infoLogger
						.info("power group " + pg.groupIndex + " power index " + n + " state = " + power.getPs());
				if (power.getPs() == PowerState.ON) {

					Environment.infoLogger.info("power group " + pg.groupIndex + "close redunt power index " + n);
					power.power(PowerState.OFF); // 关闭
				}
			}
		}

		return validCount >= minNeedPowerCount;
	}

	/**
	 * 对设备电源进行检测，当智能电源打开后将动态调整电源的个数；当抛出异常时表示电源没有正常工作，此时设备不能启动流程
	 * 
	 * @param pd
	 *            流程对象
	 * 
	 *            设定电流/最大电流 如果大于1，则为1 设定电流/最大电流*1.3 *1.5=电源个数
	 * 
	 * @throws AlertException
	 */
	public void checkPowers() throws AlertException {

		// 如果辅助电源故障，则禁止启动
		for (int n = 0; n < auxiliaryPowers.size(); n++) {

			if (auxiliaryPowers.get(n).getWs() == WorkState.FAULT) {

				throw new AlertException(AlertCode.POWER_DOWN, I18N.getVal(I18N.AuxiliaryPowerException));
			}
		}

		if (chargePowerInfo == null) {

			return; // 没有启用电源
		}

		int needPowerCount = chargePowerInfo.powerCount;

		if (mainBoard.getControlUnitCount() == 1) {
			// 设备单一流程时启用智能电源
			if (Context.getAccessoriesService().getEnergySaveData().isUseSmartPower()) {
				needPowerCount = getMinNeedPowerCount(mainBoard.getControlUnitByIndex(0).getProcedure());
			}
			Environment.infoLogger.info("minmun power need count = " + needPowerCount);
		}

		if (monitor) {
			// 检测良品电源个数,电源如果未打开则打开电源

			if (!makePowerEnough(needPowerCount)) {

				if (needPowerCount > 2) {

					needPowerCount--; // 减少余量
					if (!makePowerEnough(needPowerCount)) {

						throw new AlertException(AlertCode.POWER_DOWN,
								I18N.getVal(I18N.PowerNotEnough, needPowerCount));
					}
				} else {

					throw new AlertException(AlertCode.POWER_DOWN, I18N.getVal(I18N.PowerNotEnough, needPowerCount));
				}

			}

			// 当电源故障数太多，不打开电源
			int normalPowerCount = 0;
			for (PowerGroup pg : groups) {

				for (int n = 0; n < pg.getPowerCount(); n++) {
					if (pg.getPowerByIndex(n).getWs() == WorkState.NORMAL) {

						normalPowerCount++;
					}
				}
			}
			if (normalPowerCount < needPowerCount) { // 电源余量

				throw new AlertException(AlertCode.POWER_DOWN,
						I18N.getVal(I18N.PowerNotMeetMinNeed, normalPowerCount, needPowerCount));
			}

		}

	}

	/**
	 * 解析电源状态2
	 * 
	 * @author wavy_zheng 2021年4月24日
	 * @param psqd
	 */
	protected void decodePowerQueryData2(PowerStateQueryData2 psqd) {

		List<Byte> switches = psqd.getChargePowerSwitches();
		List<Byte> states = psqd.getChargePowerStates();
		// 检测是否发生故障

		List<InverterPower> faultPowers = new ArrayList<InverterPower>();
		List<AuxiliaryPower> auxiliaryPowers = new ArrayList<AuxiliaryPower>();

		for (int n = 0; n < psqd.getChargePowerCount(); n++) {

			InverterPower power = findPowerByIndex(n);
			power.setPs((switches.get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON : PowerState.OFF);
			WorkState oldWs = power.getWs();
			power.setWs((states.get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT : WorkState.NORMAL);

			if (oldWs == WorkState.NORMAL && power.getWs() == WorkState.FAULT) {

				// 电源发生故障
				faultPowers.add(power);

				// 读取故障详细信息
				try {

					if (product == PowerProduct.GD) {
						power.setFault(readPowerFaultInfo(getIndexBy(power)));
					} else if (product == PowerProduct.TBM) {

						power.setTmbFault(readTBMPowerFaultInfos());
					}
				} catch (AlertException e1) {

					e1.printStackTrace();
				}

				if (Context.getAccessoriesService().getEnergySaveData().isUseSmartPower()) {
					// 尝试打开同组的其他电源
					try {
						if (!openAnyOffPower(power.getGroup())) {

							if (mainBoard.getState() == State.FORMATION) {

								Context.getCoreService().emergencyPause(null);
							}
						} else {

							Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
									I18N.getVal(I18N.PowerBackup));
						}
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else if (power.getWs() == WorkState.NORMAL) {

				power.setFault(null); // 恢复正常

			}

		}

		switches = psqd.getAuxiliariePowerSwitches();
		states = psqd.getAuxiliariePowerStates();
		for (int n = 0; n < psqd.getAuxiliaryCount(); n++) {

			AuxiliaryPower power = findAuxiliaryPowerByIndex(n);
			power.setPs((switches.get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON : PowerState.OFF);
			WorkState oldWs = power.getWs();
			power.setWs((states.get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT : WorkState.NORMAL);
			if (oldWs == WorkState.NORMAL && power.getWs() == WorkState.FAULT) {

				// 电源发生故障
				auxiliaryPowers.add(power);
				if (mainBoard.getState() == State.FORMATION) {

					try {
						Context.getCoreService().emergencyPause(null);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		if (faultPowers.size() > 0) {

			StringBuffer msg = new StringBuffer(I18N.getVal(I18N.Inverter));
			for (int n = 0; n < faultPowers.size(); n++) {

				msg.append(getIndexBy(faultPowers.get(n)) + 1 + "");
				if (n < faultPowers.size() - 1) {

					msg.append(",");
				} else {

					msg.append(I18N.getVal(I18N.Breakdown));
				}
			}
			alert = true;
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN, msg.toString(), false);
			} catch (AlertException e) {

				e.printStackTrace();
			}
		}
		if (auxiliaryPowers.size() > 0) {

			alert = true;
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN,
						I18N.getVal(I18N.AuxiliaryPower) + I18N.getVal(I18N.Breakdown), false);
			} catch (AlertException e) {

				e.printStackTrace();
			}
		}

		if (faultPowers.isEmpty() && auxiliaryPowers.isEmpty() && alert) {

			alert = false;
			// 恢复报警
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN, "电源已恢复正常", true);
			} catch (AlertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 刷新数据到各个电源对象
	 * 
	 * @param psqd
	 */
	protected void decodePowerQueryData(PowerStateQueryData psqd) {

		List<Byte> switches = psqd.getChargePowerSwitches();
		List<Byte> states = psqd.getChargePowerStates();
		// 检测是否发生故障

		List<InverterPower> faultPowers = new ArrayList<InverterPower>();
		List<AuxiliaryPower> auxiliaryPowers = new ArrayList<AuxiliaryPower>();

		for (int n = 0; n < psqd.getChargePowerCount(); n++) {

			InverterPower power = findPowerByIndex(n);
			power.setPs((switches.get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON : PowerState.OFF);
			WorkState oldWs = power.getWs();
			power.setWs((states.get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT : WorkState.NORMAL);

			// 测试
			// if (n == 0 && MainBoard.startupCfg.isUseVirtualData()) {
			//
			// power.setWs(WorkState.FAULT);
			//
			// }

			if (oldWs == WorkState.NORMAL && power.getWs() == WorkState.FAULT) {

				// 电源发生故障
				faultPowers.add(power);

				// 读取故障详细信息
				try {

					if (product == PowerProduct.GD) {

						power.setFault(readPowerFaultInfo(getIndexBy(power)));
					} else if (product == PowerProduct.TBM) {

						power.setTmbFault(readTBMPowerFaultInfos());
					}

					// power.setFault(readPowerFaultInfo(getIndexBy(power)));
				} catch (AlertException e1) {

					e1.printStackTrace();
				}

				if (Context.getAccessoriesService().getEnergySaveData().isUseSmartPower()) {
					// 尝试打开同组的其他电源
					try {
						if (!openAnyOffPower(power.getGroup())) {

							if (mainBoard.getState() == State.FORMATION) {

								Context.getCoreService().emergencyPause(null);
							}
						} else {

							Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
									I18N.getVal(I18N.PowerBackup));
						}
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else if (power.getWs() == WorkState.NORMAL) {

				power.setFault(null); // 恢复正常

			}

		}

		switches = psqd.getAuxiliariePowerSwitches();
		states = psqd.getAuxiliariePowerStates();
		for (int n = 0; n < psqd.getAuxiliaryCount(); n++) {

			AuxiliaryPower power = findAuxiliaryPowerByIndex(n);
			power.setPs((switches.get(n / 8) & 0x01 << (n % 8)) > 0 ? PowerState.ON : PowerState.OFF);
			WorkState oldWs = power.getWs();
			power.setWs((states.get(n / 8) & 0x01 << (n % 8)) > 0 ? WorkState.FAULT : WorkState.NORMAL);
			if (oldWs == WorkState.NORMAL && power.getWs() == WorkState.FAULT) {

				// 电源发生故障
				auxiliaryPowers.add(power);
				if (mainBoard.getState() == State.FORMATION) {

					try {
						Context.getCoreService().emergencyPause(null);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		if (faultPowers.size() > 0) {

			StringBuffer msg = new StringBuffer(I18N.getVal(I18N.Inverter));
			for (int n = 0; n < faultPowers.size(); n++) {

				msg.append(getIndexBy(faultPowers.get(n)) + 1 + "");
				if (n < faultPowers.size() - 1) {

					msg.append(",");
				} else {

					msg.append(I18N.getVal(I18N.Breakdown));
				}
			}
			alert = true;
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN, msg.toString(), false);
			} catch (AlertException e) {

				e.printStackTrace();
			}
		}
		if (auxiliaryPowers.size() > 0) {

			alert = true;
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN,
						I18N.getVal(I18N.AuxiliaryPower) + I18N.getVal(I18N.Breakdown), false);
			} catch (AlertException e) {

				e.printStackTrace();
			}
		}

		if (faultPowers.isEmpty() && auxiliaryPowers.isEmpty() && alert) {

			alert = false;
			// 恢复报警
			try {
				Context.getAlertManager().handle(AlertCode.POWER_DOWN, "电源已恢复正常", true);
			} catch (AlertException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// System.out.println(findPowerByIndex(0).getFault());

	}

	/**
	 * 通过电源序号找到逆变电源对象
	 * 
	 * @param powerIndex
	 * @return
	 */
	public InverterPower findPowerByIndex(int powerIndex) {

		int index = 0;
		for (int n = 0; n < groups.size(); n++) {

			if (powerIndex < index + groups.get(n).getPowerCount()) {

				return groups.get(n).getPowerByIndex(powerIndex - index);
			}
			index += groups.get(n).getPowerCount();
		}
		return null;
	}

	public AuxiliaryPower findAuxiliaryPowerByIndex(int index) {

		return auxiliaryPowers.get(index);
	}

	public int getAuxiliaryPowerCount() {

		return auxiliaryPowers.size();
	}

	public int getChargePowerCount() {

		int count = 0;
		for (int n = 0; n < groups.size(); n++) {

			count += groups.get(n).getPowerCount();
		}

		return count;
	}

	/**
	 * 逆变电源待机检测
	 * 
	 * @throws AlertException
	 */
	private void checkWaitCondition(int timeElapsed) throws AlertException {

		if (mainBoard.getState() != State.FORMATION && mainBoard.getState() != State.CAL) {

			if (getPowerOnCount() != 1
					&& Context.getAccessoriesService().getEnergySaveData().getMaxPowerWaitMin() > 0) {

				waitSeconds += timeElapsed;

				if (waitSeconds >= Context.getAccessoriesService().getEnergySaveData().getMaxPowerWaitMin() * 60) {

					mainBoard.pushSendQueue(0xff, -1, AlertCode.NORMAL, "设备待机时长" + waitSeconds / 60 + "min,逆变电源进入待机状态");
					// 进入待机状态
					enterWaitState();
					waitSeconds = 0;

				}
				return;
			}

		}
		waitSeconds = 0; // 清除待机时长

	}

	private int getPowerOnCount() {

		int count = 0;
		for (PowerGroup pg : groups) {

			for (int n = 0; n < pg.getPowerCount(); n++) {

				if (pg.getPowerByIndex(n).ps == PowerState.ON) {

					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 打开同组正常未开启的电源
	 * 
	 * @param group
	 * @return true正常打开 ； false没有多余的电源可以打开了
	 * @throws AlertException
	 */
	public boolean openAnyOffPower(PowerGroup group) throws AlertException {

		for (int n = 0; n < group.getPowerCount(); n++) {

			InverterPower power = group.getPowerByIndex(n);
			if (power.getWs() == WorkState.NORMAL && power.getPs() == PowerState.OFF) {

				power.power(PowerState.ON);
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取电源的序号（编号）
	 * 
	 * @param power
	 * @return
	 */
	public int getIndexBy(InverterPower power) {

		int groupIndex = groups.indexOf(power.getGroup());
		int index = 0;
		if (groupIndex != -1) {

			for (int n = 0; n < groups.size(); n++) {

				if (groupIndex == n) {

					return index + power.getPowerIndexInGroup();

				}
				index += groups.get(n).getPowerCount();
			}
		}

		return -1;
	}

	protected SerialPort findSerialPort(int index, PowerType pt) {

		List<PowerInfo> list = new ArrayList<PowerInfo>();
		for (int n = 0; n < MainBoard.startupCfg.getPowerManagerInfo().powerInfos.size(); n++) {

			PowerInfo pi = MainBoard.startupCfg.getPowerManagerInfo().powerInfos.get(n);
			if (pi.powerType == pt) {

				list.add(pi);
			}
		}
		if (index > list.size() - 1) {

			return null;
		}

		return Context.getPortManager().getPortByName(list.get(index).portName);

	}

	/**
	 * 清除待机时长
	 */
	public void clearWaitSeconds() {

		waitSeconds = 0;
	}

	public boolean isUse() {
		return use;
	}

	/**
	 * 获取电源分组集合
	 * 
	 * @return
	 */
	public List<PowerGroup> getGroups() {
		return groups;
	}

	public void flushAllPowersRunMiliseconds(long miliseconds) {

		for (int n = 0; n < getChargePowerCount(); n++) {

			InverterPower power = findPowerByIndex(n);
			if (power.getPs() == PowerState.ON) {
				power.setRunMiliseconds(power.getRunMiliseconds() + miliseconds);
			}
		}

		for (AuxiliaryPower power : auxiliaryPowers) {

			if (power.getPs() == PowerState.ON) {
				power.setRunMiliseconds(power.getRunMiliseconds() + miliseconds);
			}
		}

	}

	public boolean isMonitor() {
		return monitor;
	}

	public int getRecovertyCount() {
		return recovertyCount;
	}

	public void setRecovertyCount(int recovertyCount) {
		this.recovertyCount = recovertyCount;
	}

}
