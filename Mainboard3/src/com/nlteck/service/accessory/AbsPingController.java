package com.nlteck.service.accessory;
/**
* @author  wavy_zheng
* @version 创建时间：2022年6月20日 下午2:26:30
* 抽象针床控制器
*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AirPressureStateData;
import com.nltecklib.protocol.li.accessory.AirPressureStateData.AirPressureState;
import com.nltecklib.protocol.li.accessory.AirValveSwitchData;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.accessory.PingStateData;
import com.nltecklib.protocol.li.accessory.PingStateData.TempProbe;
import com.nltecklib.protocol.li.accessory.ValveSwitchData;
import com.nltecklib.protocol.li.main.CylinderCfgData;
import com.nltecklib.protocol.li.main.MainEnvironment.PingStateMonitorData;

public abstract class AbsPingController {

	protected MainBoard mainboard;
	protected ScheduledExecutorService executor;
	protected PingStateData state;
	protected Date readDate;
	protected CylinderCfgData cfgData = new CylinderCfgData();
	protected AirPressureStateData airPressureState;

	public AbsPingController(MainBoard mainboard) {

		this.mainboard = mainboard;

		startMonitorState();

	}

	/**
	 * 处理温度异常
	 * 
	 * @author wavy_zheng 2022年7月19日
	 */
	private void processTempState() {

		for (TempProbe probe : state.getTempProbes()) {

			probe.tempOk = probe.temperature < MainBoard.startupCfg.getDriverTemperatureAlert();

		}

	}

	/**
	 * 开始针床状态监测
	 * 
	 * @author wavy_zheng 2022年6月20日
	 */
	private void startMonitorState() {

		if (executor == null) {

			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {
						state = readPingState(0);

						// 主控自己处理温度状态
						processTempState();
						
						//将温度赋值给通道
						int count = mainboard.getDriverBoards().size() * MainBoard.startupCfg.getDriverChnCount();
						for(int n = 0 ; n < count ; n++) {
							
							if(n < state.getTempProbes().size()) {
								
								mainboard.getChannelByChnIndex(n).setTemperature(state.getTempProbes().get(n).temperature);
							}
							
							
						}
						
						

						airPressureState = readAirPressure(0);

						readDate = new Date();

						// System.out.println("airPressureState = " +
						// airPressureState.getAirPressureState());

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}, 100, 5000, TimeUnit.MILLISECONDS);

		}

	}

	/**
	 * 检查气缸状态
	 * 
	 * @author wavy_zheng 2022年6月20日
	 * @throws Exception
	 */
	public void checkPingState() throws Exception {

		if (state == null) {

			throw new Exception("未读取到针床状态！");
		}

		if (state.isDoorCylinder1PosOk()) {

			throw new Exception("检测到门信号1异常，门未关闭!");
		}
		if (!state.isDoorCylinder2PosOk()) {

			throw new Exception("检测到门信号2异常, 门未关闭!");
		}

		if (!state.isTrayOffsetOk()) {

			throw new Exception("检测到托盘已偏移!");
		}
		if (!state.isTrayBackPosOk()) {

			throw new Exception("检测到托盘后边位置偏移!");
		}
		if (!state.isTrayFrontPosOk()) {

			throw new Exception("检测到托盘前边位置偏移!");
		}
		if (state.isTrayCylinder1PosOk()) {

			throw new Exception("托盘1号位置信号异常，未下降!");
		}
		if (!state.isTrayCylinder2PosOk()) {

			throw new Exception("托盘2号位置信号异常，未下降!");
		}
		if (state.isTrayCylinder3PosOk()) {

			throw new Exception("托盘3号位置信号异常，未下降!");
		}
		if (!state.isTrayCylinder4PosOk()) {

			throw new Exception("托盘4号位置信号异常，未下降!");
		}
		if (!state.isSmogCheck1Ok()) {

			throw new Exception("1号烟雾报警器报警");
		}
		if (!state.isSmogCheck2Ok()) {

			throw new Exception("2号烟雾报警器报警");
		}

		int count = mainboard.getDriverBoards().size() * MainBoard.startupCfg.getDriverChnCount();
		for (int n = 0; n < state.getTempProbes().size(); n++) {

			TempProbe probe = state.getTempProbes().get(n);

			if (n < count || n == state.getTempProbes().size() - 1) {
				if (!probe.tempOk) {

					throw new Exception((n + 1) + "号温度探头超温");
				}
			}
		}

		for (int n = 0; n < state.getFans().size(); n++) {

			PingStateData.Fan fan = state.getFans().get(n);
			if (!fan.open) {

				throw new Exception((n + 1) + "号散热风机未启动");
			}
			if (!fan.normal) {

				throw new Exception((n + 1) + "号散热风机异常");
			}
		}

		if (airPressureState == null) {

			throw new Exception("未读取到气压状态!");
		}

		// 检查气压状态
		if (airPressureState.getAirPressureState() == AirPressureState.LOW) {

			throw new Exception("检测到气压状态：欠压");

		} else if (airPressureState.getAirPressureState() == AirPressureState.HIGH) {

			throw new Exception("检测到气压状态：过压");

		}

		if (new Date().getTime() - readDate.getTime() > 15 * 1000) {

			throw new Exception("针床已掉线，请检查!");
		}

	}

	public PingStateData getState() {
		return state;
	}

	public AirPressureStateData getAirPressureState() {
		return airPressureState;
	}

	public CylinderCfgData getCfgData() {
		return cfgData;
	}

	public void setCfgData(CylinderCfgData cfgData) {
		this.cfgData = cfgData;
	}

	/**
	 * 创建可监控的数据
	 * 
	 * @author wavy_zheng 2022年7月19日
	 * @return
	 */
	public List<PingStateMonitorData> createPingStateMonitorData() {

		List<PingStateMonitorData> list = new ArrayList<>();

		PingStateMonitorData psmd = new PingStateMonitorData();
		psmd.doorCylinder1PosOk = state.isDoorCylinder1PosOk();
		psmd.doorCylinder2PosOk = state.isDoorCylinder2PosOk();
		psmd.smogCheck1Ok = state.isSmogCheck1Ok();
		psmd.smogCheck2Ok = state.isSmogCheck2Ok();
		psmd.trayBackPosOk = state.isTrayBackPosOk();
		psmd.trayCylinder1PosOk = state.isTrayCylinder1PosOk();
		psmd.trayCylinder2PosOk = state.isTrayCylinder2PosOk();
		psmd.trayCylinder3PosOk = state.isTrayCylinder3PosOk();
		psmd.trayCylinder4PosOk = state.isTrayCylinder4PosOk();
		psmd.trayFrontPosOk = state.isTrayFrontPosOk();
		psmd.trayOffsetOk = state.isTrayOffsetOk();
		psmd.fans = state.getFans();
		psmd.tempProbes = state.getTempProbes();
		psmd.pressureState = airPressureState == null ? AirPressureState.NORMAL
				: airPressureState.getAirPressureState();
		list.add(psmd);
		return list;
	}

	/**
	 * 查询针床控制板的状态
	 * 
	 * @author wavy_zheng 2022年6月20日
	 * @param driverIndex
	 * @return
	 */
	public abstract PingStateData readPingState(int driverIndex) throws Exception;

	/**
	 * 四色灯报警
	 * 
	 * @author wavy_zheng 2022年6月20日
	 * @param lightData
	 * @throws Exception
	 */
	public abstract void writeFourLightState(FourLightStateData lightData) throws Exception;

	/**
	 * 读取气压状态
	 * 
	 * @author wavy_zheng 2022年6月20日
	 * @param driverIndex
	 * @return
	 * @throws Exception
	 */
	public abstract AirPressureStateData readAirPressure(int driverIndex) throws Exception;

	/**
	 * 气压控制开关
	 * 
	 * @author wavy_zheng 2022年6月20日
	 * @param lift
	 *            true表示气缸抬起； false气缸降落
	 * @throws Exception
	 * 
	 */
	public abstract void writeAirPressureControl(boolean lift) throws Exception;

	/**
	 * 读取气压控制状态
	 * 
	 * @author wavy_zheng 2022年6月21日
	 * @return
	 * @throws Exception
	 */
	// public abstract AirValveSwitchData readAirPressureControl() throws Exception;

}
