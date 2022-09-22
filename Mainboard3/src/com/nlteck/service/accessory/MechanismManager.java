package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.CylinderInfo;
import com.nlteck.service.StartupCfgManager.TempProbeInfo;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.MechanismStateQueryData;
import com.nltecklib.protocol.li.main.CylinderPressureProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;

/**
 * 机械电气管理器
 * 
 * @author Administrator
 *
 */
public abstract class MechanismManager {

	protected MainBoard mainBoard;
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	protected TempProbeInfo tcInfo;
	protected CylinderInfo cylinderInfo;
	protected boolean commErr; // 通信故障

	protected int scanCount; // 扫描次数

	// 温度探头和通道的映射关系
	protected Map<Integer, List<Integer>> tempChnMap = new HashMap<Integer, List<Integer>>();

	// protected ValveState trayValve = ValveState.OPEN; // 托盘电磁阀状态
	// protected ValveState ACRelay = ValveState.OPEN; // 交流继电器状态
	// protected ValveState emergencyStop = ValveState.OPEN; // 急停开关状态

	protected MechanismStateQueryData msqd = new MechanismStateQueryData();
	protected double valveTempUpper = 0; // 机械超温上限值
	protected CylinderPressureProtectData pressureRange = new CylinderPressureProtectData();
	protected boolean exception; // 机械发生异常?

	public MechanismManager(MainBoard mb) throws AlertException {

		this.mainBoard = mb;

		int index = MainBoard.startupCfg.getMechanismManagerInfo().cylinderInfos.indexOf(new CylinderInfo(true, null));
		if (index != -1) {

			cylinderInfo = MainBoard.startupCfg.getMechanismManagerInfo().cylinderInfos.get(index);
			// 初始化
			for (int n = 0; n < cylinderInfo.count; n++) {

				msqd.getCylinderStates().add(ValveState.OPEN);
			}

		} else {

			throw new AlertException(AlertCode.INIT, "初始化机械管理器失败:没有启用气缸");
		}

		index = MainBoard.startupCfg.getMechanismManagerInfo().tempProbeInfos.indexOf(new TempProbeInfo(true, null));
		if (index != -1) {

			tcInfo = MainBoard.startupCfg.getMechanismManagerInfo().tempProbeInfos.get(index);
			for (int n = 0; n < tcInfo.count; n++) {

				msqd.getTcReads().add(0.0);
				msqd.getTcStates().add(AlertState.NORMAL);
			}

		}

		Environment.infoLogger.info("start monitor tray information");
		executor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {

				try {

					if (mainBoard.isPoweroff() || !mainBoard.isInitOk()) {

						return;
					}

					if (MainBoard.startupCfg.getMechanismManagerInfo().monitor) {

						// 读取机械状态
						MechanismStateQueryData tempMsqd = readMechanismState(0);
						// 检测是否触发保护
						try {
							checkProtection(tempMsqd);
						} finally {
							msqd = tempMsqd;
						}

					}

				} catch (AlertException e) {

					if (!exception) {

						exception = true;
						try {
							Context.getAlertManager().handle(e.getAlertCode(), e.getMessage(), false);
						} catch (AlertException e1) {

							e1.printStackTrace();
						}

					}

					e.printStackTrace();
				} catch (Throwable t) {

					Environment.errLogger.error(CommonUtil.getThrowableException(t));

				}

			}

		}, 1, 2, TimeUnit.SECONDS);

	}

	/**
	 * 打开或关闭电磁阀
	 * 
	 * @param vs
	 * @return
	 * @throws AlertException
	 */
	public abstract boolean writeValve(int driverIndex, ValveState vs) throws AlertException;

	/**
	 * 读取电磁阀状态
	 * 
	 * @param driverIndex
	 * @return
	 */
	public abstract ValveState readValve(int driverIndex) throws AlertException;

	/**
	 * 写入阀温度超温上限，下位机在温度超限后能自动松开电磁阀
	 * 
	 * @param driverIndex
	 * @param tempUpper
	 * @return
	 * @throws AlertException
	 */
	public abstract boolean writeValveTempUpper(int driverIndex, double tempUpper) throws AlertException;

	/**
	 * 读取气缸温度超限值
	 */
	public abstract double readValveTempUpper(int driverIndex) throws AlertException;

	/**
	 * 读取机械电气部分状态
	 * 
	 * @param driverIndex
	 * @return
	 * @throws AlertException
	 */
	public abstract MechanismStateQueryData readMechanismState(int driverIndex) throws AlertException;

	/**
	 * 写入压力范围
	 * 
	 * @param pressureLower
	 * @param pressureUpper
	 * @return
	 * @throws AlertException
	 */
	public abstract boolean writePressureRange(double pressureLower, double pressureUpper) throws AlertException;

	/**
	 * 读取压力正常范围
	 * 
	 * @param cpd
	 * @return
	 * @throws AlertException
	 */
	public abstract CylinderPressureProtectData readPressureRange(CylinderPressureProtectData cpd) throws AlertException;

	/**
	 * 获取缓存的机械状态数据
	 * 
	 * @return
	 */
	public MechanismStateQueryData getMsqd() {
		return msqd;
	}

	public void checkProtection(MechanismStateQueryData queryData) throws AlertException {

		if (msqd == null) {

			return;
		}
		if (queryData.getConnectState() == AlertState.ALERT && msqd.getConnectState() == AlertState.NORMAL) {

			throw new AlertException(AlertCode.PRESSURE, "气压板通信异常!");
		}
		if (queryData.getPressureState() == AlertState.ALERT && msqd.getPressureState() == AlertState.NORMAL) {

			throw new AlertException(AlertCode.PRESSURE, "运行机构气压发生异常");
		}
		if (queryData.getSmogState() == AlertState.ALERT && msqd.getSmogState() == AlertState.NORMAL) {

			throw new AlertException(AlertCode.PRESSURE, "烟雾报警器触发保护");
		}

		if (mainBoard.getState() == State.FORMATION) {

			if (queryData.getDoorState() == ValveState.OPEN && msqd.getDoorState() == ValveState.CLOSE) {

				throw new AlertException(AlertCode.PRESSURE, "注意:运行机构的门已被打开");
			}
			if (queryData.getTrayState() == ValveState.OPEN && msqd.getTrayState() == ValveState.CLOSE) {

				throw new AlertException(AlertCode.PRESSURE, "托盘被拿出");
			}
			if (queryData.getEmergencyStopState() == ValveState.OPEN
					&& msqd.getEmergencyStopState() == ValveState.CLOSE) {

				throw new AlertException(AlertCode.PRESSURE, "急停按钮被下压");
			}
		}

		// if (exception) {
		//
		// return; // 已经异常没必要做后续检测
		// }

		// 检测温度
		List<Double> readTempList = queryData.getTcReads();
		List<AlertState> readStateList = queryData.getTcStates();
		for (int n = 0; n < readStateList.size(); n++) {

			if (msqd.getTcStates().get(n) == AlertState.NORMAL && readStateList.get(n) == AlertState.ALERT) {

				throw new AlertException(AlertCode.PRESSURE,
						"机构超温[检测到当前温度" + readTempList.get(n) + "℃超过允许的最大温度" + valveTempUpper + "℃]");
			}
		}
		if (queryData.getSolenoidState() != msqd.getSolenoidState()) {

			scanCount = 3; // 大概6秒时间检测气缸限位状态
		}
		if (scanCount == 0) {

			// 检测限位开关
			for (int n = 0; n < queryData.getCylinderStates().size(); n++) {

				if (queryData.getCylinderStates().get(n) != queryData.getSolenoidState()) {

					throw new AlertException(AlertCode.PRESSURE, "检测到电磁阀已执行"
							+ (queryData.getSolenoidState() == ValveState.CLOSE ? "闭合" : "张开") + ",但机构并未执行到位,请检查");

				}
			}
		}

		if (scanCount > 0) {

			scanCount--;
		}

		// 检测是否恢复正常
		if (queryData.getConnectState() == AlertState.NORMAL && queryData.getPressureState() == AlertState.NORMAL
				&& queryData.getSmogState() == AlertState.NORMAL) {

			if (mainBoard.getState() == State.FORMATION) {

				if (queryData.getDoorState() != ValveState.CLOSE || queryData.getTrayState() != ValveState.CLOSE
						|| queryData.getEmergencyStopState() != ValveState.CLOSE) {

					return; // 有异常
				}
			}
			// 最终检测所有机械温度
			readStateList = queryData.getTcStates();
			boolean allTempCheckOk = true;
			for (int n = 0; n < readStateList.size(); n++) {

				if (readStateList.get(n) == AlertState.ALERT) {

					allTempCheckOk = false;
					break;
				}
			}
			if (allTempCheckOk) {

				if (exception) {
					exception = false; // 消除报警
					try {
						Context.getAlertManager().handle(AlertCode.PRESSURE, "机构恢复正常", true);
					} catch (AlertException e1) {

						e1.printStackTrace();
					}
				}

			}

		}

	}

	private void initTempChnMap() {

		List<Integer> chnIndexList = new ArrayList<Integer>();
		for (int n = 0; n < 4; n++) {
			chnIndexList.add(n);
		}
		for (int n = 16; n < 20; n++) {
			chnIndexList.add(n);
		}
		for (int n = 32; n < 36; n++) {
			chnIndexList.add(n);
		}
		for (int n = 48; n < 52; n++) {
			chnIndexList.add(n);
		}
		for (int n = 64; n < 68; n++) {
			chnIndexList.add(n);
		}
		for (int n = 80; n < 84; n++) {
			chnIndexList.add(n);
		}
		for (int n = 96; n < 100; n++) {
			chnIndexList.add(n);
		}
		for (int n = 112; n < 116; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(0, chnIndexList);

		// 第2个探头
		chnIndexList = new ArrayList<Integer>();
		for (int n = 12; n < 16; n++) {
			chnIndexList.add(n);
		}
		for (int n = 28; n < 32; n++) {
			chnIndexList.add(n);
		}
		for (int n = 44; n < 48; n++) {
			chnIndexList.add(n);
		}
		for (int n = 60; n < 64; n++) {
			chnIndexList.add(n);
		}
		for (int n = 76; n < 80; n++) {
			chnIndexList.add(n);
		}
		for (int n = 92; n < 96; n++) {
			chnIndexList.add(n);
		}
		for (int n = 108; n < 112; n++) {
			chnIndexList.add(n);
		}
		for (int n = 124; n < 128; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(1, chnIndexList);

		// 第3个探头
		chnIndexList = new ArrayList<Integer>();
		for (int n = 4; n < 8; n++) {
			chnIndexList.add(n);
		}
		for (int n = 20; n < 24; n++) {
			chnIndexList.add(n);
		}
		for (int n = 36; n < 40; n++) {
			chnIndexList.add(n);
		}
		for (int n = 52; n < 56; n++) {
			chnIndexList.add(n);
		}
		for (int n = 68; n < 72; n++) {
			chnIndexList.add(n);
		}
		for (int n = 84; n < 88; n++) {
			chnIndexList.add(n);
		}
		for (int n = 100; n < 104; n++) {
			chnIndexList.add(n);
		}
		for (int n = 116; n < 120; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(2, chnIndexList);

		// 第4个探头
		chnIndexList = new ArrayList<Integer>();
		for (int n = 8; n < 12; n++) {
			chnIndexList.add(n);
		}
		for (int n = 24; n < 28; n++) {
			chnIndexList.add(n);
		}
		for (int n = 40; n < 44; n++) {
			chnIndexList.add(n);
		}
		for (int n = 56; n < 60; n++) {
			chnIndexList.add(n);
		}
		for (int n = 72; n < 76; n++) {
			chnIndexList.add(n);
		}
		for (int n = 88; n < 92; n++) {
			chnIndexList.add(n);
		}
		for (int n = 104; n < 108; n++) {
			chnIndexList.add(n);
		}
		for (int n = 120; n < 124; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(3, chnIndexList);

		// 第5个探头
		chnIndexList = new ArrayList<Integer>();
		for (int n = 132; n < 136; n++) {
			chnIndexList.add(n);
		}
		for (int n = 148; n < 152; n++) {
			chnIndexList.add(n);
		}
		for (int n = 164; n < 168; n++) {
			chnIndexList.add(n);
		}
		for (int n = 180; n < 184; n++) {
			chnIndexList.add(n);
		}
		for (int n = 196; n < 200; n++) {
			chnIndexList.add(n);
		}
		for (int n = 228; n < 232; n++) {
			chnIndexList.add(n);
		}
		for (int n = 244; n < 248; n++) {
			chnIndexList.add(n);
		}
		for (int n = 212; n < 216; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(4, chnIndexList);

		// 第6个探头
		chnIndexList = new ArrayList<Integer>();
		for (int n = 136; n < 140; n++) {
			chnIndexList.add(n);
		}
		for (int n = 152; n < 156; n++) {
			chnIndexList.add(n);
		}
		for (int n = 168; n < 172; n++) {
			chnIndexList.add(n);
		}
		for (int n = 184; n < 188; n++) {
			chnIndexList.add(n);
		}
		for (int n = 200; n < 204; n++) {
			chnIndexList.add(n);
		}
		for (int n = 216; n < 220; n++) {
			chnIndexList.add(n);
		}
		for (int n = 232; n < 236; n++) {
			chnIndexList.add(n);
		}
		for (int n = 248; n < 252; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(5, chnIndexList);
		chnIndexList.clear();

		chnIndexList = new ArrayList<Integer>();
		// 第7个探头
		for (int n = 128; n < 132; n++) {
			chnIndexList.add(n);
		}
		for (int n = 144; n < 148; n++) {
			chnIndexList.add(n);
		}
		for (int n = 160; n < 164; n++) {
			chnIndexList.add(n);
		}
		for (int n = 176; n < 180; n++) {
			chnIndexList.add(n);
		}
		for (int n = 192; n < 196; n++) {
			chnIndexList.add(n);
		}
		for (int n = 208; n < 212; n++) {
			chnIndexList.add(n);
		}
		for (int n = 224; n < 228; n++) {
			chnIndexList.add(n);
		}
		for (int n = 240; n < 244; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(6, chnIndexList);

		chnIndexList = new ArrayList<Integer>();
		// 第8个探头
		for (int n = 140; n < 144; n++) {
			chnIndexList.add(n);
		}
		for (int n = 156; n < 160; n++) {
			chnIndexList.add(n);
		}
		for (int n = 172; n < 176; n++) {
			chnIndexList.add(n);
		}
		for (int n = 188; n < 192; n++) {
			chnIndexList.add(n);
		}
		for (int n = 204; n < 208; n++) {
			chnIndexList.add(n);
		}
		for (int n = 220; n < 224; n++) {
			chnIndexList.add(n);
		}
		for (int n = 236; n < 240; n++) {
			chnIndexList.add(n);
		}
		for (int n = 252; n < 256; n++) {
			chnIndexList.add(n);
		}
		tempChnMap.put(7, chnIndexList);

	}

	/**
	 * 发生机械保护?
	 * 
	 * @return
	 */
	public boolean isException() {
		return exception;
	}
    
	/**
	 * 所有气缸是否全部打开
	 * @return
	 */
	public boolean isAllCylinderOpen() {

		// 检测限位开关
		for (int n = 0; n < msqd.getCylinderStates().size(); n++) {

			if (msqd.getCylinderStates().get(n) != ValveState.OPEN) {

				    return false;

			}
		}
		return true;
	}

}
