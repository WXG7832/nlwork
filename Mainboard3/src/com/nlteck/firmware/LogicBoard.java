package com.nlteck.firmware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.ParameterName;
import com.nlteck.i18n.I18N;
import com.nlteck.service.connector.LogicConnector;
import com.nlteck.service.connector.STM32LogicConnector;
import com.nlteck.service.connector.VirtualLogicConnector;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.logic.LogicCalMatchData;
import com.nltecklib.protocol.li.logic.LogicCalProcessData;
import com.nltecklib.protocol.li.logic.LogicCalculateData;
import com.nltecklib.protocol.li.logic.LogicChnStopData;
import com.nltecklib.protocol.li.logic.LogicDeviceProtectData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.li.logic.LogicFaultCheckData;
import com.nltecklib.protocol.li.logic.LogicFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKCalculateData;
import com.nltecklib.protocol.li.logic.LogicHKCalibrateData;
import com.nltecklib.protocol.li.logic.LogicHKFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPoleData;
import com.nltecklib.protocol.li.logic.LogicStateData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.DriverFaultInfo;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
//import com.nltecklib.protocol.li.main.OverChargeProtectData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.li.workform.CalExFlashWriteData;
import com.nltecklib.protocol.li.workform.CalFlashWriteData;
import com.nltecklib.protocol.li.workform.CalHKCalculateData;
import com.nltecklib.protocol.li.workform.CalHKCalibrateData;
import com.nltecklib.protocol.li.workform.CalHKFlashWriteData;
import com.nltecklib.protocol.li.workform.CalLogicModeData;
import com.nltecklib.protocol.li.workform.CalProcessData;
import com.nltecklib.protocol.li.workform.CalculateData;
import com.nltecklib.protocol.li.workform.LogicBaseVoltageData;
import com.nltecklib.protocol.li.workform.WorkformEnvironment;
import com.rm5248.serial.SerialPort;

public class LogicBoard {

	private SerialPort serialPort; // 通信口
	private List<DriverBoard> drivers = new ArrayList<DriverBoard>();

	public static final int MAX_CAL_COUNT = 4; // 最大校准板数量
	public static final double LOGIC_CAL_VOLT_MAX_OFFSET = 200; // 最大基准电压偏差值
	public static final String checkBoardFlag = "01010101"; // 校准板对接详情
	public static final double LOGIC_CAL_CONVERT_RATE = 2.732; // 逻辑板ADC(单位0.01mA/mV)转校准板基准电压转换率
	private Map<Integer, Double> baseVoltMap = new HashMap<Integer, Double>(); // 基准电压集合
	
	private int logicIndex;
	
	private boolean waitForStepSkip = false; // 等待步次跳转

	private boolean noneBattery = true;// 默认为无电池状态
	private PickupState pickState = PickupState.UDT; // 采集初始状态
	private State operateState = State.NORMAL; // 用户操作状态
	private LogicState state = LogicState.UDT;
	

	private int loopIndex = 0; // 循环序号，从1开始
	private int stepIndex = 0; // 当前流程步次号，从1开始

	private int continueCCStep; // 0表示不是涓流，1表示首步次涓流，2表示中间涓流，3表示结尾涓流


	private Date lastCommunicateTime; // 上次与上位机通信的时间
	private boolean boardConnected = true; // 下面逻辑板通信中断
	private boolean use; // 使用情况
	private long pickupTimeSpan; // 采集时间间隔
	private boolean reverseDriverIndex; // 驱动板反序
	private long commTimeout; //通信超时时间，单位ms
	private int  driverEnableFlag = 0xffff; //驱动板启用情况
	private String   uuid;  //唯一标识码

	private ProcedureData procedure; // 逻辑板绑定的流程
	private ParameterName pn = new ParameterName(WorkType.AG); // 逻辑板绑定的


	// 准备推送缓存区
	private Map<Channel, List<ChannelData>> pushReadyBuffer = new ConcurrentHashMap<Channel, List<ChannelData>>();

	private int pickupDriverIndex = 0; // 当前采集板号
	private long recvPickupCount; // 当前逻辑板采集次数统计
	private long sendPickupCount; // 当前逻辑板发送采样次数统计
	private long lastPickupCount; // 上一次逻辑板采集次数统计
	
	private long runMiliseconds;  //逻辑板累计运行时长

	/**
	 * 主控通过推送编号和回馈编号来决定是否暂停数据推送
	 */
	private long pushDataIndex = 0; // 数据推送编号
	private long pushResponseDataIndex = 0; // 数据推送回馈编号
	
	private PickupData  lastPickupData = null; //上一次未被推送成功的数据

	private ScheduledExecutorService executor = null;

	private LogicConnector logicConnector; // 逻辑板通信控制器

	private Date procedureStartTime;
	
	private FaultCheckData faultCheckData = new FaultCheckData();
	private String         softversion;
	private boolean        programBurnOk = true; //程序烧录
	private boolean        commOk; //通信正常?
  
	
	
	
	/**
	 * 逻辑板故障数据 false 故障 true 正常
	 * 
	 * @return
	 * 
	 */
	public static class FaultCheckData {

		public boolean SRAM_OK = false;
		public boolean AD1_OK = false;
		public boolean AD2_OK = false;
		public boolean AD3_OK = false;
		public boolean FLASH_OK = false;
		public boolean AD1_CAL_OK = false;
		public boolean AD2_CAL_OK = false;
		public boolean AD3_CAL_OK = false;
	}

	public MainBoard getMainBoard() {
		return mb;
		
	}

	public ControlUnit getControlUnit() {

		for (ControlUnit cu : mb.getControls()) {

			if (cu.getLogics().contains(this)) {

				return cu;
			}
		}

		return null;

	}

	/**
	 * 基本产品定义
	 */

	private Timer timer; // 计时器
	private Timer optTimer; // 操作定时器
	private MainBoard mb;

	public LogicBoard(MainBoard mb, int logicIndex, SerialPort port) {

		this.mb = mb;
		this.logicIndex = logicIndex;
		this.serialPort = port;
		use = MainBoard.startupCfg.getLogicInfo(logicIndex).use;
		reverseDriverIndex = MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex;
		pickupTimeSpan = MainBoard.startupCfg.getLogicInfo(logicIndex).pickupTime;
		commTimeout = MainBoard.startupCfg.getLogicInfo(logicIndex).communicateTimeout;
		driverEnableFlag = MainBoard.startupCfg.getLogicInfo(logicIndex).enableFlag;
		
		System.out.println("driverEnableFlag = " + driverEnableFlag);
		 
       
	}

	public synchronized void stopPickup() {

		System.out.println("executor == null ? " + executor == null);
		if (executor != null /* && !executor.isShutdown() */ && isUse()) {
			System.out.println("destroy logic pick thread : " + logicIndex);

			executor.shutdownNow();
			executor = null;
			boardConnected = false; // 置位通信标志
		}

	}
	
	
	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * 通信是否正常
	 * 
	 * @return
	 */
	private boolean isCommError() {

		if (recvPickupCount - lastPickupCount == 0) {

			// 如果一轮采集全部失败，则认为通信发生异常
			return true;
		}
		return false;
	}


	

	

	/**
	 * 所有通道是否都处于准备状态
	 * 
	 * @return
	 */
	public boolean isAllChnReadyToStartup() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (db.getChannel(i).getState() != ChnState.UDT && db.getChannel(i).getState() != ChnState.NONE
						&& db.getChannel(i).getState() != ChnState.CLOSE) {

					return false;
				}
			}

		}

		return true;
	}

	/**
	 * 所有暂停的通道是否全部恢复为启动
	 * 
	 * @return
	 */
	public boolean isAllChnResumed() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (db.getChannel(i).getState() == ChnState.PAUSE) {

					return false;
				}
			}

		}

		return true;
	}

	/**
	 * 该分区所有通道是否进入暂停状态
	 * 
	 * @return
	 */
	public boolean isAllChnPaused() {

		boolean once = false;
		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (db.getChannel(i).getState() == ChnState.RUN) {

					return false;
				} else if (db.getChannel(i).getState() == ChnState.PAUSE) {

					once = true;
				}
			}

		}

		return once;
	}

	/**
	 * 至少有个通道已运行
	 * 
	 * @return
	 */
	public boolean isAnyChnRunning() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (db.getChannel(i).getState() == ChnState.RUN) {

					return true;
				}
			}

		}
		return false;

	}

	public boolean isAnyChnUDT() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (db.getChannel(i).getState() == ChnState.UDT) {

					return true;
				}
			}

		}
		return false;

	}

	

	/**
	 * 通道的实时运行数据区是否已经清空，此项作为通道是否完成的依据;
	 * 
	 * @return
	 */
	public boolean isAllChnRuntimeCachesEmpty() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				if (!db.getChannel(i).isRuntimeCachesEmpty()) {

					return false;
				}
			}

		}

		return true;
	}

	public boolean isAllChnOver() {

		for (DriverBoard db : getDrivers()) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				if (/* db.getChannel(i).getState() == ChnState.UDT || */ db.getChannel(i).getState() == ChnState.RUN
						|| db.getChannel(i).getState() == ChnState.PAUSE) {

					return false;
				}
			}

		}

		return true;

	}


	public Channel findChnByIndexInLogic(int chnIndexInLogic) {

		int driverIndex = chnIndexInLogic / MainBoard.startupCfg.getDriverChnCount();
		int chnIndexInDriver = chnIndexInLogic % MainBoard.startupCfg.getDriverChnCount();
		return drivers.get(driverIndex).getChannel(chnIndexInDriver);

	}

	/**
	 * 处理驱动板温度及状态
	 * 
	 * @param lpd
	 */
	private void processDriverData(int driverIndex, LogicPickupData lpd) {

//		DriverState dd = new DriverState();
//		dd.setState(DrivingState.NORMAL);
//		dd.setResistorTemp1(lpd.getTemperature1());
//		dd.setResistorTemp2(lpd.getTemperature2());
//		dd.setDriverIndex(lpd.getUnitIndex() * MainBoard.startupCfg.getLogicDriverCount() + driverIndex);

//		getMainBoard().addDriverTemperature(
//				lpd.getUnitIndex() * MainBoard.startupCfg.getLogicDriverCount() + driverIndex, dd);
	}

	// /**
	// * 逻辑板数据采集线程
	// */
	// @Deprecated
	// private void startPickupWork(int sleepTime) {
	//
	// logicController.setRecvTimeout(sleepTime);
	//
	// for (int i = 0; i < drivers.size(); i++) {
	//
	// try {
	//
	// int boardIndex = i;
	// if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {
	// // 处理板号
	// boardIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - i;
	// }
	//
	// LogicPickupData lpd = logicController.pickup(boardIndex);
	//
	// if (lpd == null) {
	//
	// Environment.errLogger.info(
	// "pickup data from logicboard " + logicIndex + "driver index = " + boardIndex
	// + " failed");
	// continue;
	// } else if (lpd.getPickupState() == PickupState.CAL) { // 逻辑板已进入校准模式，直接退出采样
	//
	// System.out.println("calibrate mode ,exit pickup thread!");
	// // mainBoard.state = State.CAL;
	// pickTimer.cancel();
	// pickTimer = null;
	// return;
	// }
	//
	// // System.out.println(lpd);
	// processChnRunning(lpd); // 处理通道状态
	//
	// // 处理驱动板状态及温度
	// processDriverData(lpd);
	//
	// // 生成PC采集数据包并推入缓存队列
	// pushToDataQueue(lpd);
	//
	// drivers.get(i).setPickState(lpd.getPickupState()); // 更新采集状态
	//
	// } catch (AlertException e) {
	//
	// AlertData alertData = new AlertData();
	// alertData.setAlertCode(e.getAlertCode());
	// alertData.setAlertInfo(e.getMessage());
	// alertData.setChnIndex(e.getChannel().getLogicChnIndex());
	// alertData.setDate(new Date());
	// alertData.setUnitIndex(logicIndex + 0x10);
	// // 设备报警
	// getMainBoard().pushSendQueue(new AlertDecorator(alertData));
	//
	// } catch (Exception e) {
	//
	// System.out.println(CommonUtil.getThrowableException(e));
	// }
	// }
	//
	// // 运行中设备状态变更
	// if ((state == State.FORMATION || state == State.PAUSE || state ==
	// State.STARTUP || state == State.STOP)
	// && stepIndex > 0) {
	//
	// // 检测所有通道的状态
	// if (isAllChnOver()) {
	//
	// if (operateState == State.STOP) {
	//
	// if (state != State.STOP) {
	//
	// // 需要判断实时运行缓存区是否已经清空
	// if (isAllChnRuntimeCachesEmpty()) {
	//
	// setState(State.STOP);
	// try {
	// triggerStopListener();
	// } catch (AlertException e) {
	//
	// }
	// }
	//
	// }
	// } else {
	//
	// // 该通道已全部完成
	// if (state != State.COMPLETE) {
	//
	// // 判断所有通道是否全部结束
	// // 需要判断实时运行缓存区是否已经清空
	// if (isAllChnRuntimeCachesEmpty()) {
	//
	// setState(State.COMPLETE);
	// try {
	// triggerCompleteListener();
	// } catch (AlertException e) {
	// // mainBoard.pushSendQueue(e);
	// }
	// }
	// }
	// }
	//
	// } else if (isAllChnPaused()) {
	//
	// if (state != State.PAUSE) {
	//
	// setState(State.PAUSE);
	// try {
	// triggerPauseListener();
	// } catch (AlertException e) {
	// // mainBoard.pushSendQueue(e);
	// }
	// }
	//
	// }
	// }
	//
	// if (!getMainBoard().isOffline()) {
	// // 推送数据到PC机
	// try {
	// pushPickupDataToPC();
	// } catch (AlertException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	/**
	 * 初始化逻辑板,检测逻辑板状态
	 */
	public void init() throws AlertException {

		/*for (int i = 0; i < MainBoard.startupCfg.getLogicDriverCount(); i++) {

			DriverBoard driver = null;

			if (!MainBoard.startupCfg.isUseVirtualData()) {
				driver = new STMDriverBoard(i, mb , null);
			} else {
				driver = new VirtualDriverBoard(i, mb);
			}
			driver.init();
			drivers.add(driver);
		}*/


	}

	

	/**
	 * 采集逻辑板最新的数据
	 * 
	 * @return
	 */
	public List<ChannelData> fetchData() throws AlertException {

		List<ChannelData> list = new LinkedList<ChannelData>();
		for (int n = 0; n < drivers.size(); n++) {

			list.addAll(drivers.get(n).fetchData());
		}
		return list;
	}

	public Channel getChannelByLogicChnIndex(int logicChnIndex) {

		int driverChnCount = MainBoard.startupCfg.getDriverChnCount();
		return drivers.get(logicChnIndex / driverChnCount).getChannel(logicChnIndex % driverChnCount);
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public List<DriverBoard> getDrivers() {
		return drivers;
	}

	public int getLoopIndex() {
		return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public boolean isReadyToWork() {

		for (DriverBoard db : drivers) {

			if (!db.isReadyToWork())
				return false;
		}

		return true;
	}

	/**
	 * 特殊处理板上下颠倒的情况
	 * 
	 * @param states
	 * @return
	 */
	private List<Byte> specialSwitchState(List<Byte> states) {

		List<Byte> list = new ArrayList<Byte>();

		for (int i = states.size() - 1; i >= 0; i--) {

			list.add(states.get(i));
		}

		return list;

	}

	/**
	 * 设备逻辑板的通道开关状态,1打开，0关闭
	 * 
	 * @param states
	 * @throws IOException
	 */
	public void setChannelSwitchState(List<Byte> states) throws AlertException {

		// 配置
		// LogicChnSwitchData lcsd = new LogicChnSwitchData();
		// if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {
		//
		// states = specialSwitchState(states);
		// }
		// lcsd.setChannelStates(states);
		// lcsd.setUnitIndex(logicIndex);
		// StringBuffer err = new StringBuffer();
		// if (!logicController.configChnSwitchState(lcsd, err)) {
		//
		// AlertData alertData = new AlertData();
		// alertData.setAlertCode(AlertCode.LOGIC);
		// alertData.setAlertInfo("初始化逻辑板" + (logicIndex + 1) + "通道状态发生逻辑错误");
		// alertData.setUnitIndex(0x10 + logicIndex);
		// alertData.setDate(new Date());
		// mainBoard.pushSendQueue(new AlertDecorator(alertData));
		// }
		//
		// if (logicIndex == 1) {
		//
		// states = specialSwitchState(states);
		// }
		for (int n = 0; n < states.size(); n++) {

			// 每个字节代表8个通道
			for (int m = 0; m < 8; m++) {

				int chnIndexInUnit = m + n * 8;
				int driverIndex = chnIndexInUnit / MainBoard.startupCfg.getDriverChnCount();
				int chnIndexInDriver = chnIndexInUnit % MainBoard.startupCfg.getDriverChnCount();
				Channel chn = getDrivers().get(driverIndex).getChannel(chnIndexInDriver);
				if ((states.get(n) & (0x01 << m)) == 0) {

					// 关闭通道
					chn.setState(ChnState.CLOSE);

				} else {

					chn.setState(ChnState.UDT);

				}
			}

		}

	}

	/**
	 * 设置设备保护
	 * 
	 * @throws AlertException
	 */
	public void configDeviceProtect(DeviceProtectData dpd) throws AlertException {
		// 设备保护

		if (!MainBoard.startupCfg.isUseVirtualData()) {
			LogicDeviceProtectData ldpd = new LogicDeviceProtectData();
			ldpd.setUnitIndex(logicIndex);
			ldpd.setBatteryProtectVoltage(dpd.getBatVoltUpper());
			ldpd.setDeviceProtectVoltage(dpd.getDeviceVoltUpper());
			ldpd.setDeviceProtectCurrent(dpd.getCurrUpper());
			StringBuffer err = new StringBuffer();
			if (!logicConnector.configDeviceProtect(ldpd, err)) {

//				throw new AlertException(AlertCode.LOGIC, "设置逻辑板" + (logicIndex + 1) + "设备保护返回错误");
				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.LogicBoardProtectSetError, logicIndex + 1));
			}
		}

	}

	/**
	 * 设置极性保护值
	 * 
	 * @param pd
	 * @throws IOException
	 */
	public void configPoleProtect(PoleData pd) throws AlertException {

		if (!MainBoard.startupCfg.isUseVirtualData()) {
			LogicPoleData lpd = new LogicPoleData();
			lpd.setUnitIndex(logicIndex);
			lpd.setPole(Pole.values()[pd.getPole().ordinal()]);
			lpd.setVoltageBound(pd.getPoleDefine());

			StringBuffer err = new StringBuffer();

			if (!logicConnector.configPole(lpd, err)) {

//				throw new AlertException(AlertCode.COMM_ERROR, "配置逻辑板" + (logicIndex + 1) + "极性发生通信错误");
				throw new AlertException(AlertCode.COMM_ERROR,
						I18N.getVal(I18N.LogicBoardPoleSetError, logicIndex + 1));
			}

		}

	}

	public int getLogicIndex() {
		return logicIndex;
	}

	/**
	 * 获取当前逻辑板运行模式
	 * 
	 * @return
	 */
	// public WorkMode getLogicWorkMode(int stepIndex) {
	//
	// if (control.getProcedure() == null)
	// return null;
	// Step step = control.getProcedureStep(stepIndex);
	//
	// if (step.workMode == MainEnvironment.WorkMode.CCC) {
	//
	// if (stepIndex < control.getProcedureStepCount()) {
	//
	// // 判断下一步次是不是CV
	// if (control.getProcedureStep(stepIndex + 1).workMode ==
	// MainEnvironment.WorkMode.CVC) {
	//
	// return WorkMode.CC_CV;
	// }
	// }
	//
	// return WorkMode.CC;
	//
	// } else if (step.workMode == MainEnvironment.WorkMode.CVC) {
	//
	// return WorkMode.CC_CV;
	// } else if (step.workMode == MainEnvironment.WorkMode.CCD) {
	//
	// return WorkMode.DC;
	// } else {
	//
	// return WorkMode.UDT;
	// }
	// }
    
	public static StepAndLoop skipPreviousStep(ProcedureData pd, int stepIndex, int loopIndex) {
		
		if (loopIndex == 0) {

			loopIndex = 1;
		}
		StepAndLoop sl = new StepAndLoop();
		sl.nextStep = stepIndex;
		sl.nextLoop = loopIndex;
		if (pd.getLoopCount() > 0) {

			// 处理循环步次
			if (stepIndex == pd.getLoopSt() && loopIndex > 1) {
				// 跳转循环

				sl.nextStep = pd.getLoopEd();
				sl.nextLoop = loopIndex - 1;
				return sl;

			}

		}
		// 检测上一步次是否CC
		if (stepIndex >  2 && pd.getStep(stepIndex - 2).workMode == MainEnvironment.WorkMode.CVC) {

			sl.nextStep = stepIndex - 2; // CC-CV为一个整体，故直接跳2步
		} else {
			sl.nextStep = stepIndex - 1;
		}

		return sl;
		
	}
	
	/**
	 * 转步次
	 */
	public static StepAndLoop skipNextStep(ProcedureData pd, int stepIndex, int loopIndex) {

		if (loopIndex == 0) {

			loopIndex = 1;
		}
		StepAndLoop sl = new StepAndLoop();
		sl.nextStep = stepIndex;
		sl.nextLoop = loopIndex;
		if (pd.getLoopCount() > 0) {

			// 处理循环步次
			if (stepIndex == pd.getLoopEd() && loopIndex <= pd.getLoopCount()) {
				// 跳转循环

				sl.nextStep = pd.getLoopSt();
				sl.nextLoop = loopIndex + 1;
				return sl;

			}

		}
		// 检测下一步次是否CV
		if (stepIndex <= pd.getStepCount() - 1 && pd.getStep(stepIndex).workMode == MainEnvironment.WorkMode.CVC) {

			sl.nextStep = stepIndex + 2; // CC-CV为一个整体，故直接跳2步
		} else {
			sl.nextStep = stepIndex + 1;
		}

		return sl;

	}
	
	/**
	 * 将所有通道重置为装备测试状态
	 */
	private void resetAllChannelReady() {

		System.out.println("reset all channel!");
		for (DriverBoard db : drivers) {

			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				ChnState state = db.getChannel(i).getState();
				if ((state != ChnState.NONE && state != ChnState.CLOSE)) {

					db.getChannel(i).reset(); // 重置通道

				}
			}
		}
	}



	public Date getProcedureStartTime() {
		return procedureStartTime;
	}

	public PickupState getPickState() {
		return pickState;
	}

	public void setPickState(PickupState pickState) {
		this.pickState = pickState;
	}

	/**
	 * 配置并检测基准电压
	 * 
	 * @param boardIndex
	 * @return
	 * @throws IOException
	 */
	public boolean configCalBoardMatched(int driverIndex, int boardIndex, short matchFlag) throws AlertException {

		Environment.infoLogger
				.info("driverIndex = " + driverIndex + ",boardIndex = " + boardIndex + ",matchFlag = " + matchFlag);

		int chnIndex = -1, logicChnIndex = -1;
		try {
			for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

				chnIndex = i;
				if ((matchFlag & (0x01 << i)) > 0) {

					Environment.infoLogger.info("set cal board base voltage : " + baseVoltMap.get(boardIndex));
					boolean ok = false;
					try {
						ok = getMainBoard().getDeviceController().setCalBoardBaseVoltage(boardIndex, i, WorkState.WORK,
								baseVoltMap.get(boardIndex));
					} catch (IOException e) {

						e.printStackTrace();
//						throw new AlertException(AlertCode.COMM_ERROR, "设置校准板基准电压发生错误");
						throw new AlertException(AlertCode.COMM_ERROR,I18N.getVal(I18N.CalBoardBaseVoltSetFail));
					}

					if (!ok) {

//						throw new AlertException(AlertCode.COMM_ERROR, "设置校准板基准电压发生错误");
						throw new AlertException(AlertCode.COMM_ERROR,I18N.getVal(I18N.CalBoardBaseVoltSetFail));
					}

					Environment.infoLogger.info("set logic board voltage check state");
					// 设置逻辑板通道基准电压
					if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

						//driverIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
					}
					logicChnIndex = driverIndex * MainBoard.startupCfg.getDriverChnCount() + i;
					logicConnector.configBaseVoltage(logicIndex, logicChnIndex, Pole.NORMAL, MatchState.MATCHED);
					CommonUtil.sleep(500);
					// double voltage = logicConnector.readBaseVoltage(logicIndex,
					// driverIndex * MainBoard.startupCfg.getDriverChnCount() + i);

					if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

						//driverIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
					}

					// Environment.infoLogger.info("logicIndex = " + logicIndex + ",chnIndex="
					// + (driverIndex * MainBoard.startupCfg.getDriverChnCount() + i) +
					// ",baseVoltage = "
					// + baseVoltMap.get(boardIndex) + ",readVoltage=" + voltage + ",final voltage =
					// "
					// + voltage * LOGIC_CAL_CONVERT_RATE);
					// if (Math.abs(baseVoltMap.get(boardIndex)
					// - voltage * LOGIC_CAL_CONVERT_RATE) > LOGIC_CAL_VOLT_MAX_OFFSET) {
					//
					// throw new AlertException(AlertCode.LOGIC,
					// "逻辑板(" + (driverIndex + 1) + ")未与校准板(" + (boardIndex + 1) + ")对接;\n通道" + (i +
					// 1)
					// + "读取的基准电压" + voltage + " * " + LOGIC_CAL_CONVERT_RATE + "与校准板设置基准电压"
					// + baseVoltMap.get(boardIndex) + "偏差( " + LOGIC_CAL_VOLT_MAX_OFFSET + ")过大");
					//
					// }

				}

			}
		} finally {

			// 退出基准匹配模式
			try {
				getMainBoard().getDeviceController().setCalBoardBaseVoltage(boardIndex, chnIndex, WorkState.UNWORK,
						baseVoltMap.get(boardIndex));

				logicConnector.configBaseVoltage(logicIndex, logicChnIndex, Pole.NORMAL, MatchState.NORMAL);

			} catch (IOException e) {

				e.printStackTrace();
//				throw new AlertException(AlertCode.COMM_ERROR, "退出校准板基准电压匹配模式发生错误");
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CalBoardBaseVoltMatchExitFail));

			}
		}

		return true;

	}

	/**
	 * 下发单点HK校准信息
	 * 
	 * @author wavy_zheng 2020年4月23日
	 * @param chcd
	 * @return
	 * @throws AlertException
	 */
	public boolean configHKCalibration(CalHKCalibrateData chcd) throws AlertException {

		LogicHKCalibrateData data = new LogicHKCalibrateData();
		int chnIndex = chcd.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}
		data.setUnitIndex(chcd.getUnitIndex());
		data.setChnIndex(chnIndex);
		data.setWorkMode(chcd.getWorkMode());
		data.setRange(chcd.getRange());

		data.setProgramI1(chcd.getProgramI1());
		data.setProgramV1(chcd.getProgramV1());
		data.setProgramV2(chcd.getProgramV2());
		data.setProgramI2(chcd.getProgramI2());
		data.setProgramV3(chcd.getProgramV3());
		data.setProgramI3(chcd.getProgramI3());

		data.setAdcList(chcd.getAdcList());

		StringBuffer err = new StringBuffer();
		// Logger logger;
		// try {
		// logger = LogUtil.createLog("log/calibration");
		// logger.info(data);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (!logicConnector.configHKLogicCalibration(data, err)) {

			AlertData alertData = new AlertData();
			alertData.setAlertCode(AlertCode.LOGIC);
//			alertData.setAlertInfo("校准逻辑板" + (logicIndex + 1) + "发生逻辑错误");
			alertData.setAlertInfo(I18N.getVal(I18N.CalLogicBoardCauseLogicError,logicIndex + 1));
			alertData.setUnitIndex(0x10 + logicIndex);
			alertData.setDate(new Date());
			getMainBoard().pushSendQueue(new AlertDecorator(alertData));
			return false;
		}

		return true;
	}

	/**
	 * 下发单点校准信息
	 * 
	 * @param cpd
	 * @return
	 * @throws AlertException
	 */
	public boolean configLogicMode(CalLogicModeData clmd) throws AlertException {

		LogicCalProcessData lcpd = new LogicCalProcessData();

		int chnIndex = clmd.getChnIndex();

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}
		lcpd.setChnIndex(chnIndex);
		lcpd.setPole(Pole.values()[clmd.getPole().ordinal()]);
		lcpd.setProgramI(clmd.getProgramI());
		lcpd.setProgramV(clmd.getProgramV());
		lcpd.setWorkMode(clmd.getWorkMode());
		lcpd.setUnitIndex(clmd.getUnitIndex());
		lcpd.setOrient(clmd.getOrient());
		lcpd.setHighPrecision(clmd.getPrecision() == 1);

		StringBuffer err = new StringBuffer();

		if (!logicConnector.setLogicCalProcess(lcpd, err)) {

			AlertData alertData = new AlertData();
			alertData.setAlertCode(AlertCode.LOGIC);
//			alertData.setAlertInfo("校准逻辑板" + (logicIndex + 1) + "发生逻辑错误");
			alertData.setAlertInfo(I18N.getVal(I18N.CalLogicBoardCauseLogicError,logicIndex + 1));
			alertData.setUnitIndex(0x10 + logicIndex);
			alertData.setDate(new Date());
			getMainBoard().pushSendQueue(new AlertDecorator(alertData));
			return false;
		}

		return true;

	}

	/**
	 * 模块使能
	 * 
	 * @param enable
	 * @return
	 * @throws AlertException
	 */
	public void enableModule(int chnIndexInLogic, boolean enable) throws AlertException {

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndexInLogic = specialProcessChnIndex(chnIndexInLogic);
		}
		logicConnector.enableModule(chnIndexInLogic, enable);

	}

	/**
	 * 读取校准数据
	 * 
	 * @author wavy_zheng 2020年4月23日
	 * @param chnIndex
	 * @return
	 * @throws AlertException
	 */
	public CalHKCalibrateData readHKCalibration(int chnIndex) throws AlertException {

		LogicHKCalibrateData data = new LogicHKCalibrateData();
		data.setUnitIndex(logicIndex);

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}

		data.setChnIndex(chnIndex);

		StringBuffer err = new StringBuffer();

		LogicHKCalibrateData response = logicConnector.readLogicHKCalibration(data, err);

		// 转化
		if (response == null) {

			throw new RuntimeException(err.toString());
		}

		chnIndex = response.getChnIndex();

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(response.getChnIndex());
		}

		// 组装成上位机识别的协议
		CalHKCalibrateData ccd = new CalHKCalibrateData();
		ccd.setUnitIndex(response.getUnitIndex());
		ccd.setChnIndex(chnIndex);
		ccd.setWorkMode(response.getWorkMode());
		ccd.setRange(response.getRange());
		ccd.setAdcList(response.getAdcList());

		return ccd;

	}

	/**
	 * 查询逻辑板ADC状态
	 * 
	 * @param chnIndex
	 * @return
	 * @throws AlertException
	 */
	public CalProcessData getCalProcess(int chnIndex) throws AlertException {

		LogicCalProcessData lcpd = new LogicCalProcessData();
		lcpd.setUnitIndex(logicIndex);

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}

		lcpd.setChnIndex(chnIndex);

		StringBuffer err = new StringBuffer();
		LogicCalProcessData response = logicConnector.getLogicCalProcess(lcpd, err);

		// 转化
		if (response == null) {

			throw new RuntimeException(err.toString());
		}

		chnIndex = response.getChnIndex();

		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(response.getChnIndex());
		}

		CalProcessData cpd = new CalProcessData();
		cpd.setUnitIndex(response.getUnitIndex());
		cpd.setChnIndex(chnIndex);
		cpd.setPole(WorkformEnvironment.Pole.values()[response.getPole().ordinal()]);
		cpd.setPrimitiveADC(response.getPrimitiveADC());
		// cpd.setProgramI(response.getProgramI());
		// cpd.setProgramV(response.getProgramV());
		cpd.setReady(response.getReady());
		cpd.setWorkMode(response.getWorkMode());
		cpd.setResult(response.getResult());
		cpd.setOrient(response.getOrient());

		return cpd;

	}

	/**
	 * 将逻辑板的驱动板位置上下颠倒
	 * 
	 * @param chnIndex
	 * @return
	 */
	private static int specialProcessChnIndex(int chnIndex) {

		int driverIndex = chnIndex / MainBoard.startupCfg.getDriverChnCount();
		int chnIndexInDriver = chnIndex % MainBoard.startupCfg.getDriverChnCount();
		//driverIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;

		return driverIndex * MainBoard.startupCfg.getDriverChnCount() + chnIndexInDriver;
	}

	public CalHKFlashWriteData readHKCalFlash(CalHKFlashWriteData readData) throws AlertException {

		// 转化为logic
		LogicHKFlashWriteData lfwd = new LogicHKFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = readData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}
		StringBuffer err = new StringBuffer();
		LogicHKFlashWriteData response = logicConnector.readHKCalFlash(logicIndex, chnIndex, err);

		if (response != null) {

			if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

				// 特殊处理通道号
				chnIndex = specialProcessChnIndex(response.getChnIndex());

			}
			// 转成PC认识的协议格式
			CalHKFlashWriteData cfwd = new CalHKFlashWriteData();
			cfwd.setChnIndex(chnIndex);
			cfwd.setUnitIndex(logicIndex);
			cfwd.setCcDots(response.getCcDots());
			cfwd.setCvDots(response.getCvDots());
			cfwd.setDcDots(response.getDcDots());
			cfwd.setDvDots(response.getDvDots());

			return cfwd;

		}

		return null;

	}

	public CalExFlashWriteData readCalFlashEx(CalExFlashWriteData readData) throws AlertException {

		// 转化为logic
		LogicFlashWriteData lfwd = new LogicFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = readData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}
		StringBuffer err = new StringBuffer();
		LogicFlashWriteData response = logicConnector.readCalFlash(logicIndex, chnIndex, err);

		if (response != null) {

			if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

				// 特殊处理通道号
				chnIndex = specialProcessChnIndex(response.getChnIndex());

			}
			// 转成PC认识的协议格式
			CalExFlashWriteData cfwd = new CalExFlashWriteData();
			cfwd.setChnIndex(chnIndex);
			cfwd.setUnitIndex(logicIndex);
			cfwd.setCcDots(response.getCcDots());
			cfwd.setCvpDots(response.getCvpDots());
			cfwd.setCvnDots(response.getCvnDots());
			cfwd.setDcDots(response.getDcDots());

			return cfwd;

		}

		return null;

	}

	public CalFlashWriteData readCalFlash(CalFlashWriteData readData) throws AlertException {

		// 转化为logic
		LogicFlashWriteData lfwd = new LogicFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = readData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}
		StringBuffer err = new StringBuffer();
		LogicFlashWriteData response = logicConnector.readCalFlash(logicIndex, chnIndex, err);

		if (response != null) {

			if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

				// 特殊处理通道号
				chnIndex = specialProcessChnIndex(response.getChnIndex());

			}
			// 转成PC认识的协议格式
			CalFlashWriteData cfwd = new CalFlashWriteData();
			cfwd.setChnIndex(chnIndex);
			cfwd.setUnitIndex(logicIndex);
			cfwd.getDotMap().get("cc").addAll(response.getCcDots());
			cfwd.getDotMap().get("cvp").addAll(response.getCvpDots());
			cfwd.getDotMap().get("cvn").addAll(response.getCvnDots());
			cfwd.getDotMap().get("dc").addAll(response.getDcDots());
//			cfwd.setCcDots(response.getCcDots());
//			cfwd.setCvpDots(response.getCvpDots());
//			cfwd.setCvnDots(response.getCvnDots());
//			cfwd.setDcDots(response.getDcDots());

			return cfwd;

		}

		return null;

	}

	/**
	 * 写入HK校准数据到flash
	 * 
	 * @author wavy_zheng 2020年4月23日
	 * @param writeData
	 * @return
	 * @throws AlertException
	 */
	public boolean writeHKCalFlash(CalHKFlashWriteData writeData) throws AlertException {

		LogicHKFlashWriteData lfwd = new LogicHKFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = writeData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}
		lfwd.setChnIndex(chnIndex);
		lfwd.setCcDots(writeData.getCcDots());
		lfwd.setCvDots(writeData.getCvDots());
		lfwd.setDcDots(writeData.getDcDots());
		lfwd.setDvDots(writeData.getDvDots());

		StringBuffer err = new StringBuffer();
		return logicConnector.writeHKCalFlash(lfwd, err);

	}

	public boolean writeCalFlashEx(CalExFlashWriteData writeData) throws AlertException {

		// 转化为logicFlashWriteData
		LogicFlashWriteData lfwd = new LogicFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = writeData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}

		lfwd.setChnIndex(chnIndex);
		lfwd.setCcDots(writeData.getCcDots());
		lfwd.setCvpDots(writeData.getCvpDots());
		lfwd.setCvnDots(writeData.getCvnDots());
		lfwd.setDcDots(writeData.getDcDots());

		StringBuffer err = new StringBuffer();
		return logicConnector.writeCalFlash(lfwd, err);

	}

	public boolean writeCalFlash(CalFlashWriteData writeData) throws AlertException {

		// 转化为logicFlashWriteData
		LogicFlashWriteData lfwd = new LogicFlashWriteData();
		lfwd.setUnitIndex(logicIndex);
		int chnIndex = writeData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);

		}

		lfwd.setChnIndex(chnIndex);

		lfwd.setCcDots(writeData.getDotMap().get("cc"));
		lfwd.setCvpDots(writeData.getDotMap().get("cvp"));
		lfwd.setCvnDots(writeData.getDotMap().get("cvn"));
		lfwd.setDcDots(writeData.getDotMap().get("dc"));

//		lfwd.setCcDots(writeData.getCcDots());
//		lfwd.setCvpDots(writeData.getCvpDots());
//		lfwd.setCvnDots(writeData.getCvnDots());
//		lfwd.setDcDots(writeData.getDcDots());

		StringBuffer err = new StringBuffer();
		return logicConnector.writeCalFlash(lfwd, err);

	}

	/**
	 * 逻辑板配置计量点
	 * 
	 * @param calData
	 * @return
	 * @throws AlertException
	 */
	public boolean writeCalculateData(CalculateData calData) throws AlertException {

		LogicCalculateData lcd = new LogicCalculateData();
		lcd.setUnitIndex(calData.getUnitIndex());
		int chnIndex = calData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}
		lcd.setChnIndex(chnIndex);
		lcd.setWorkMode(calData.getWorkMode());
		lcd.setCalculateDot(calData.getCalculateDot());
		lcd.setPole(Pole.values()[calData.getPole().ordinal()]);
		lcd.setReady((calData.getReady() & 0x02) > 0);
		return logicConnector.writeCalculateData(lcd);
	}

	/**
	 * HK逻辑板配置计量点
	 * 
	 * @author wavy_zheng 2020年4月23日
	 * @param calData
	 * @return
	 * @throws AlertException
	 */
	public boolean writeHKCalculation(CalHKCalculateData calData) throws AlertException {

		LogicHKCalculateData lcd = new LogicHKCalculateData();
		lcd.setUnitIndex(calData.getUnitIndex());
		int chnIndex = calData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}
		lcd.setChnIndex(chnIndex);
		lcd.setWorkMode(calData.getWorkMode());
		lcd.setCalculateDot(calData.getCalculateDot());
		lcd.setChunks(calData.getChunks());
		lcd.setProgramValRange(calData.getProgramValRange());
		lcd.setProgramValRead(calData.getProgramValRead());
		lcd.setProgramB1(calData.getProgramB1());
		lcd.setProgramB2(calData.getProgramB2());
		lcd.setProgramB3(calData.getProgramB3());
		lcd.setProgramK1(calData.getProgramK1());
		lcd.setProgramK2(calData.getProgramK2());
		lcd.setProgramK3(calData.getProgramK3());

		return logicConnector.writeHKCalculation(lcd);
	}

	public CalculateData readCalculateData(CalculateData calData) {

		LogicCalculateData lcd = new LogicCalculateData();
		lcd.setUnitIndex(calData.getUnitIndex());

		int chnIndex = calData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}

		lcd.setChnIndex(chnIndex);
		LogicCalculateData response = null;
		try {
			response = logicConnector.readCalculateData(calData.getUnitIndex(), chnIndex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);
		}
		if (response != null) {

			CalculateData cd = new CalculateData();
			cd.setChnIndex(chnIndex);

			cd.setUnitIndex(calData.getUnitIndex());
			cd.setWorkMode(response.getWorkMode());
			cd.setCalculateDot(response.getCalculateDot());
			cd.setFinalAdc(response.getFinalAdc());
			cd.setPole(WorkformEnvironment.Pole.values()[response.getPole().ordinal()]);
			cd.setReady((byte) (response.isReady() ? 0x01 : 0x00));
			cd.setAdcK(response.getAdcK());
			cd.setAdcB(response.getAdcB());
			cd.setPrimitiveADC(response.getPrimitiveADC());
			cd.setProgramB(response.getProgramB());
			cd.setProgramK(response.getProgramK());
			cd.setProgramVal(response.getProgramVal());

			return cd;
		}
		return null;

	}

	/**
	 * 读取HK计量点ADC
	 * 
	 * @author wavy_zheng 2020年4月23日
	 * @param calData
	 * @return
	 * @throws AlertException
	 */
	public CalHKCalculateData readHKCalculateData(CalHKCalculateData calData) throws AlertException {

		LogicHKCalculateData lcd = new LogicHKCalculateData();
		lcd.setUnitIndex(calData.getUnitIndex());

		int chnIndex = calData.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}

		lcd.setChnIndex(chnIndex);
		LogicHKCalculateData response = null;

		response = logicConnector.readHKCalculation(calData.getUnitIndex(), chnIndex);
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			// 特殊处理通道号
			chnIndex = specialProcessChnIndex(chnIndex);
		}
		if (response != null) {

			CalHKCalculateData data = new CalHKCalculateData();
			data.setUnitIndex(response.getUnitIndex());
			data.setChnIndex(chnIndex);
			data.setWorkMode(response.getWorkMode());
			data.setCalculateDot(response.getCalculateDot());
			data.setChunks(response.getChunks());
			data.setAdcK(response.getAdcK());
			data.setAdcB(response.getAdcB());
			data.setProgramValRange(response.getProgramValRange());
			data.setProgramValRead(response.getProgramValRead());
			data.setProgramValRead2(response.getProgramValRead2());
			data.setProgramValRead3(response.getProgramValRead3());
			data.setProgramB1(response.getProgramB1());
			data.setProgramB2(response.getProgramB2());
			data.setProgramB3(response.getProgramB3());
			data.setProgramK1(response.getProgramK1());
			data.setProgramK2(response.getProgramK2());
			data.setProgramK3(response.getProgramK3());

			return data;

		}

		return null;

	}

	public LogicConnector getLogicController() {
		return logicConnector;
	}

	public void setLogicController(LogicConnector logicController) {
		this.logicConnector = logicController;
	}

	/**
	 * 等待步次结束
	 * 
	 * @return
	 */
	// public boolean waitForStepOver() {
	// return waitForStepSkip;
	// }

	public State getOperateState() {
		return operateState;
	}

	public void setOperateState(State operateState) {
		this.operateState = operateState;
	}

	public boolean isNoneBattery() {
		return noneBattery;
	}

	public void setNoneBattery(boolean noneBattery) {
		this.noneBattery = noneBattery;
	}

	public Timer getTimer() {
		return timer;
	}

	/**
	 * 关闭指定的通道
	 * 
	 * @param chnIndexInLogic
	 * @return
	 * @throws AlertException
	 */
	public boolean closeChn(int chnIndexInLogic) throws AlertException {

		int driverIndex = chnIndexInLogic / MainBoard.startupCfg.getDriverChnCount();
		int chnIndexInDriver = chnIndexInLogic % MainBoard.startupCfg.getDriverChnCount();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			//driverIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
		}

		LogicChnStopData lcsd = new LogicChnStopData();
		lcsd.setUnitIndex(logicIndex);
		lcsd.setDriverIndex(driverIndex);
		lcsd.setSelectChns((short) (0x01 << chnIndexInDriver));
		StringBuffer err = new StringBuffer();

		// 关闭已选中的通道
		return logicConnector.stopChn(lcsd, err);

	}

	public void cancelAllTimers() {

		if (timer != null) {

			timer.cancel();
		}
		if (optTimer != null) {

			optTimer.cancel(); // 取消操作定时器
		}
	}

	public void setProcedureStartTime(Date procedureStartTime) {
		this.procedureStartTime = procedureStartTime;
	}

	// 步次变更等待信号;同个时刻只能有一条线程拥有权限;如果未等待则转为
	public boolean isWaitForStepSkip() {
		return waitForStepSkip;
	}

	public void setWaitForStepSkip(boolean waitForStepSkip) {
		this.waitForStepSkip = waitForStepSkip;
	}

	public int getContinueCCStep() {
		return continueCCStep;
	}

	public void setContinueCCStep(int continueCCStep) {
		this.continueCCStep = continueCCStep;
	}

	public synchronized boolean addPushReadyList(ChannelData chnData) {

		Channel key = (Channel) chnData.getDataFromKey("channel");
		List<ChannelData> list = new ArrayList<ChannelData>();
		if (pushReadyBuffer.containsKey(key)) {

			list = pushReadyBuffer.get(key);
		}
		list.add(chnData);
		pushReadyBuffer.put(key, list); // 放入准备推送缓存区

		return true;
	}

	public synchronized boolean isChnPushReady(Channel chn) {

		return pushReadyBuffer.containsKey(chn);
	}

	public List<ChannelData> getPushedChannelData(Channel chn) {

		return pushReadyBuffer.get(chn);
	}

	public List<ChannelData> removePushedChannelData(Channel chn) {

		return pushReadyBuffer.remove(chn);
	}

	public Date getLastCommunicateTime() {
		return lastCommunicateTime;
	}

	public void setLastCommunicateTime(Date lastCommunicateTime) {
		this.lastCommunicateTime = lastCommunicateTime;
	}

	/**
	 * 清空发生缓存区
	 */
	public void clearPushBuffer() {

		pushReadyBuffer.clear();
	}

	/**
	 * 启动整个分区内所有符合条件的通道
	 * 
	 * @throws AlertException
	 */
	public void startup() throws AlertException {

		for (DriverBoard db : drivers) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				db.getChannel(i).askNextStep();
			}
		}
	}

	/**
	 * 恢复整个分区内所有符合条件的通道
	 */
	public void resume() {

		for (DriverBoard db : drivers) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				db.getChannel(i).resumeStep(false);
			}
		}
	}

	/**
	 * 停止整个分区内所有符合条件的通道
	 */
	public void stop() {

		for (DriverBoard db : drivers) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				db.getChannel(i).stopStep();
			}
		}
	}

	/**
	 * 暂停整区所有符合条件的通道
	 * 
	 * @throws AlertException
	 */
	public void pause() throws AlertException {

		for (DriverBoard db : drivers) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				db.getChannel(i).pauseStep();
			}
		}
	}

	/**
	 * 清空上一次测试的缓存
	 */
	public void clearAllDeadCaches() {

		for (DriverBoard db : drivers) {

			for (int i = 0; i < db.getChannelCount(); i++) {

				db.getChannel(i).clearAllDeadCaches();
				db.getChannel(i).clearAlertCaches();
			}
		}
	}

	public void saveAllProtections() throws AlertException {

		// protectConfigManager.writeProtectFile(this);
	}

	public void writeProcedureFile() throws AlertException {

		// procedureConfigManager.writeProcedureFile(this, procedure);
	}

	/**
	 * 从硬盘加载所有配置文件
	 * 
	 * @throws AlertException
	 */
	public void loadAllParamsFromFile() throws AlertException {

		// procedureConfigManager.readProcedureFile(this);
		// protectConfigManager.readProtectFile(this);
	}

	public boolean isBoardConnected() {
		return boardConnected;
	}

	public void setBoardConnected(boolean boardConnected) {
		this.boardConnected = boardConnected;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	/**
	 * 确认收到推送回复消息
	 */
	public synchronized void revcPushResponseData() {

		pushResponseDataIndex = pushDataIndex;
	}

	/**
	 * 上次数据是否正常推送
	 * 
	 * @return
	 */
	public boolean isLastPushedDataCompleted() {

		return pushResponseDataIndex == pushDataIndex;
	}

	public long getPushResponseDataIndex() {
		return pushResponseDataIndex;
	}

	public long getPushDataIndex() {
		return pushDataIndex;
	}

	public long getRecvPickupCount() {
		return recvPickupCount;
	}

	/**
	 * 产生驱动板虚拟温度
	 * 
	 * @param temp1
	 * @param temp2
	 */
	public void updateAllVirtualDriverTemp(double temp1, double temp2) {

		if (logicConnector instanceof VirtualLogicConnector) {

			((VirtualLogicConnector) logicConnector).updateAllDriverTemp(temp1, temp2);
		}
	}

	public FaultCheckData getFaultCheckData() {
		return faultCheckData;
	}

	/**
	 * 配置逻辑板基准电压
	 * 
	 * @param cbvd
	 * @throws AlertException
	 */
	public void configBaseVoltage(LogicBaseVoltageData lbvd) throws AlertException {

		Environment.infoLogger.info(lbvd);
		int chnIndex = lbvd.getChnIndex();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			chnIndex = specialProcessChnIndex(chnIndex);
		}
		logicConnector.configBaseVoltage(lbvd.getUnitIndex(), chnIndex, lbvd.getPole(), lbvd.getMatchState());

	}

	/**
	 * 读取逻辑板基准电压值
	 * 
	 * @param lbvd
	 * @return
	 * @throws AlertException
	 */
	public LogicBaseVoltageData readBaseVoltage(LogicBaseVoltageData lbvd) throws AlertException {

		Environment.infoLogger.info(lbvd);

		LogicBaseVoltageData response = new LogicBaseVoltageData();
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			lbvd.setChnIndex(specialProcessChnIndex(lbvd.getChnIndex()));
		}

		LogicCalMatchData lcmd = logicConnector.readBaseVoltage(lbvd.getUnitIndex(), lbvd.getChnIndex());
		Environment.infoLogger.info(lcmd);
		response.setUnitIndex(lcmd.getUnitIndex());
		if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {

			lcmd.setChnIndex(specialProcessChnIndex(lcmd.getChnIndex()));
		}
		response.setChnIndex(lcmd.getChnIndex());
		response.setPole(lcmd.getPole());
		response.setMatchState(lcmd.getMatchState());
		response.setAdc(lcmd.getAdc());

		return response;
	}

	public ProcedureData getProcedure() {
		return procedure;
	}

	public void setProcedure(ProcedureData procedure) {
		this.procedure = procedure;
	}

	public ParameterName getPn() {
		return pn;
	}

	public void setPn(ParameterName pn) {
		this.pn = pn;
	}

	/**
	 * 同步状态更新
	 */
	private void updateSyncStepState() {

		getControlUnit().updateSyncWaitState();

	}

	public long getSendPickupCount() {
		return sendPickupCount;
	}


	public long getPickupTimeSpan() {
		return pickupTimeSpan;
	}

	public void setPickupTimeSpan(long pickupTimeSpan) {
		this.pickupTimeSpan = pickupTimeSpan;
	}


	public void setSendPickupCount(long sendPickupCount) {
		this.sendPickupCount = sendPickupCount;
	}
	
	public int getPickupDriverIndex() {
		return pickupDriverIndex;
	}

	public void setPickupDriverIndex(int pickupDriverIndex) {
		this.pickupDriverIndex = pickupDriverIndex;
	}
	
	
	/**
	 * 因驱动板接线问题导致序号会出现正序和反序两种接法 获取实际驱动板号
	 * 
	 * @author wavy_zheng 2020年10月24日
	 * @param driverIndex
	 *            软件上显示的驱动板号
	 * @return 实际真正下发的驱动板号
	 */
	public int getActualDriverIndex(int driverIndex) {
       
		if (reverseDriverIndex) {

			//return MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
		}
		return driverIndex;
	}
	
	public void setRecvPickupCount(long recvPickupCount) {
		this.recvPickupCount = recvPickupCount;
	}

	public PickupData getLastPickupData() {
		return lastPickupData;
	}

	public void setLastPickupData(PickupData lastPickupData) {
		this.lastPickupData = lastPickupData;
	}
	
	/**
	 * 发送次数+1
	 * @author  wavy_zheng
	 * 2020年11月26日
	 */
	public void plusPushSendCount() {
		
		pushDataIndex++;
	}

	public long getCommTimeout() {
		return commTimeout;
	}

	public void setCommTimeout(long commTimeout) {
		this.commTimeout = commTimeout;
	}

	public boolean isReverseDriverIndex() {
		return reverseDriverIndex;
	}
	
	public List<Channel> getChannels() {
		
		List<Channel> channels = new ArrayList<>();
		for(DriverBoard db : drivers) {
			
			for(int n = 0 ; n < db.getChannelCount() ; n++) {
				
				channels.add(db.getChannel(n));
			}
		}
		
		return channels;
	}

	public int getDriverEnableFlag() {
		return driverEnableFlag;
	}

	

	public String getSoftversion() {
		return softversion;
	}

	public void setSoftversion(String softversion) {
		this.softversion = softversion;
	}

	public boolean isProgramBurnOk() {
		return programBurnOk;
	}

	public void setProgramBurnOk(boolean programBurnOk) {
		this.programBurnOk = programBurnOk;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LogicState getState() {
		return state;
	}

	public void setState(LogicState state) {
		this.state = state;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	public boolean isCommOk() {
		return commOk;
	}

	public void setCommOk(boolean commOk) {
		this.commOk = commOk;
	}
  
	

}
