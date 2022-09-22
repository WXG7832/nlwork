package com.nltecklib.protocol.power.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.power.Environment.Code;


public class MainEnvironment {

	public enum AlertCode {

		NORMAL("0000"), VOLT_UPPER("2001"), VOLT_LOWER("2002"), CUR_UPPER("2003"), CUR_LOWER("2004"), CAPACITY_UPPER(
				"2005"), TIME_OVER(
						"2006"), TEMP_OVER("2007"), POLE_REVERSE("2008"), OFFPOWER("2009"), CROSS_CIRCUIT("2010")
		/** 交叉短路 */
		,MC_OVER("2011"),
		/** 弱电流超限 */
		VOLT_WAVE("2012"), CURR_WAVE("2013"), DEVICE_ERROR("2014"), COMM_ERROR("2015"), OVER_CHARGE(
				"2016"), OVER_DISCHARGE("2017"), OFFLINE("2018"), OVER_VOLT_OFFSET("2019"), TOUCH("2020"), PRESSURE(
						"2021"), LOGIC("2022"), INIT("2023"), FAN("2024"), LOGIC_BOARD(
								"2025"), CHECK_BOARD("2026"), POWER_DOWN("2027"), HEAT_OVER("2028");

		private String code;

		private AlertCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		@Override
		public String toString() {

			switch (this) {
			case NORMAL:
				return "正常";
			case VOLT_UPPER:
				return "电压超上限";
			case VOLT_LOWER:
				return "电压超下限";
			case CUR_UPPER:
				return "电流超上限";
			case CUR_LOWER:
				return "电流超下限";
			case CAPACITY_UPPER:
				return "容量超限";
			case TIME_OVER:
				return "时间超限";
			case TEMP_OVER:
				return "温度超限";
			case POLE_REVERSE:
				return "极性反接";
			case OFFPOWER:
				return "断电";
			case CROSS_CIRCUIT:
				return "交叉短路";
			case VOLT_WAVE:
				return "电压波动";
			case CURR_WAVE:
				return "电流波动";
			case DEVICE_ERROR:
				return "硬件故障";
			case COMM_ERROR:
				return "通信故障";
			case OVER_CHARGE:
				return "电芯过充";
			case OVER_DISCHARGE:
				return "电芯保护";
			case OFFLINE:
				return "设备掉线";
			case OVER_VOLT_OFFSET:
				return "压差过大";
			case TOUCH:
				return "接触保护";
			case PRESSURE:
				return "压力保护";
			case LOGIC:
				return "逻辑错误";
			case INIT:
				return "初始化失败";
			case FAN:
				return "风机故障";
			case LOGIC_BOARD:
				return "逻辑板故障";
			case CHECK_BOARD:
				return "回检板故障";
			case POWER_DOWN:
				return "电源故障";
			case HEAT_OVER:
				return "过温保护";
			case MC_OVER:
				return "弱电流保护";
			}
			
			return "";

		}

	}
	/**
	 * 报警码
	 * 
	 * @author Administrator
	 *
	 */
	// public enum AlertCode {
	//
	// NORMAL, VOLT_UPPER, VOLT_LOWER, CUR_UPPER, CUR_LOWER, CAPACITY_UPPER,
	// TIME_OVER, TEMP_OVER, POLE_REVERSE,
	// OFFPOWER, CROSS_CIRCUIT, MC_OVER, VOLT_WAVE, CURR_WAVE, DEVICE_ERROR,
	// COMM_ERROR, OVER_CHARGE, OVER_DISCHARGE,
	// OFFLINE, OVER_VOLT_OFFSET, TOUCH, PRESSURE, LOGIC, INIT, FAN, LOGIC_BOARD,
	// CHECK_BOARD, POWER_DOWN, HEAT_OVER;
	//
	// @Override
	// public String toString() {
	//
	// switch (this) {
	// case NORMAL:
	// return "正常";
	// case VOLT_UPPER:
	// return "电压超上限";
	// case VOLT_LOWER:
	// return "电压超下限";
	// case CUR_UPPER:
	// return "电流超上限";
	// case CUR_LOWER:
	// return "电流超下限";
	// case CAPACITY_UPPER:
	// return "容量超限";
	// case TIME_OVER:
	// return "时间超限";
	// case TEMP_OVER:
	// return "温度超限";
	// case POLE_REVERSE:
	// return "极性反接";
	// case OFFPOWER:
	// return "断电";
	// case CROSS_CIRCUIT:
	// return "交叉短路";
	// case VOLT_WAVE:
	// return "电压波动";
	// case CURR_WAVE:
	// return "电流波动";
	// case DEVICE_ERROR:
	// return "硬件故障";
	// case COMM_ERROR:
	// return "通信故障";
	// case OVER_CHARGE:
	// return "电芯过充";
	// case OVER_DISCHARGE:
	// return "电芯保护";
	// case OFFLINE:
	// return "设备掉线";
	// case OVER_VOLT_OFFSET:
	// return "压差过大";
	// case TOUCH:
	// return "接触保护";
	// case PRESSURE:
	// return "压力保护";
	// case LOGIC:
	// return "逻辑错误";
	// case INIT:
	// return "初始化失败";
	// case FAN:
	// return "风机故障";
	// case LOGIC_BOARD:
	// return "逻辑板故障";
	// case CHECK_BOARD:
	// return "回检板故障";
	// case POWER_DOWN:
	// return "电源故障";
	// case HEAT_OVER:
	// return "过温保护";
	// case MC_OVER:
	// return "弱电流保护";
	// }
	//
	// return "";
	// }
	// }

	/**
	 * 设备或分区状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum State {

		NORMAL, FORMATION, PAUSE, STOP, MAINTAIN, CAL, ALERT, CLOSE, COMPLETE, STARTUP, INIT, JOIN/*对接模式*/ , UPGRADE/*在线升级*/;

	}

	/**
	 * 通道状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum ChnState {

		NONE, UDT, RUN, PAUSE, STOP, CLOSE, ALERT, COMPLETE;
	}

	/**
	 * 工作模式
	 * 
	 * @author Administrator
	 *
	 */
	public enum WorkMode {

		SLEEP, CCC, CVC, CCD, CC_CV , SYNC/*增加同步模式*/;
	}

	/**
	 * 步次结束模式
	 */
	public enum OverMode {

		PROCEED, PAUSE, STOP
	}

	public enum SwitchState {

		OPEN, CLOSE, ALERT;

		public String toString() {

			switch (this) {
			case ALERT:
				return "异常";
			case CLOSE:
				return "闭合";
			case OPEN:
				return "松开";
			}
			return "";
		}
	}

	/**
	 * 流程模式
	 * 
	 * @author Administrator
	 *
	 */
	public enum ProcedureMode {

		DEVICE, LOGIC, DRIVER;

		@Override
		public String toString() {

			switch (this) {

			case DEVICE:
				return "设备";
			case LOGIC:
				return "逻辑";
			case DRIVER:
				return "驱动板";
			default:
				return "";
			}
		}

	}
	
	/**
	 * 夹具机类型
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
	public enum FixtureType {
		
		MINI_PIEF , ACT , PIEF , DOUBLE_ACT/*双层ACT*/
		
	}
	
	/**
	 * 夹具接口，用于解耦
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
	public interface Fixture {
		
		
	}
	
	/**
	 * 双层夹具单元模型
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
	public static class DoubleFixture implements Fixture{
		
		private int index;
		private boolean press; //是否压紧
		private boolean batExist1; //是否有料
		private boolean batExist2; //是否有料
		private boolean movingIn1;
		private boolean movingIn2;
		private boolean procedureRequest; //流程请求
		private boolean movingOut1;
		private boolean movingOut2;
		private boolean testing; //正在测试
		private boolean auto; //自动or手动
		private boolean disabled; //被屏蔽
		private boolean temperatureOk;
		private boolean pressureChange;
		private boolean complete; //完成信号
		private boolean formation; //化成?
		private boolean allNg; //全部Ng
		private boolean completeSoon; //预完成信号
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public boolean isPress() {
			return press;
		}
		public void setPress(boolean press) {
			this.press = press;
		}
		public boolean isBatExist1() {
			return batExist1;
		}
		public void setBatExist1(boolean batExist1) {
			this.batExist1 = batExist1;
		}
		public boolean isBatExist2() {
			return batExist2;
		}
		public void setBatExist2(boolean batExist2) {
			this.batExist2 = batExist2;
		}
		public boolean isMovingIn1() {
			return movingIn1;
		}
		public void setMovingIn1(boolean movingIn1) {
			this.movingIn1 = movingIn1;
		}
		public boolean isMovingIn2() {
			return movingIn2;
		}
		public void setMovingIn2(boolean movingIn2) {
			this.movingIn2 = movingIn2;
		}
		public boolean isProcedureRequest() {
			return procedureRequest;
		}
		public void setProcedureRequest(boolean procedureRequest) {
			this.procedureRequest = procedureRequest;
		}
		public boolean isMovingOut1() {
			return movingOut1;
		}
		public void setMovingOut1(boolean movingOut1) {
			this.movingOut1 = movingOut1;
		}
		public boolean isMovingOut2() {
			return movingOut2;
		}
		public void setMovingOut2(boolean movingOut2) {
			this.movingOut2 = movingOut2;
		}
		public boolean isTesting() {
			return testing;
		}
		public void setTesting(boolean testing) {
			this.testing = testing;
		}
		public boolean isAuto() {
			return auto;
		}
		public void setAuto(boolean auto) {
			this.auto = auto;
		}
		public boolean isDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		public boolean isTemperatureOk() {
			return temperatureOk;
		}
		public void setTemperatureOk(boolean temperatureOk) {
			this.temperatureOk = temperatureOk;
		}
		public boolean isPressureChange() {
			return pressureChange;
		}
		public void setPressureChange(boolean pressureChange) {
			this.pressureChange = pressureChange;
		}
		public boolean isComplete() {
			return complete;
		}
		public void setComplete(boolean complete) {
			this.complete = complete;
		}
		public boolean isFormation() {
			return formation;
		}
		public void setFormation(boolean formation) {
			this.formation = formation;
		}
		public boolean isAllNg() {
			return allNg;
		}
		public void setAllNg(boolean allNg) {
			this.allNg = allNg;
		}
		public boolean isCompleteSoon() {
			return completeSoon;
		}
		public void setCompleteSoon(boolean completeSoon) {
			this.completeSoon = completeSoon;
		}
		
		
	}
	

	/**
	 * 转步模式
	 */
	public enum StepMode {

		/**
		 * ASYNC : 充放异步 SYNC: 充放同步
		 */
		ASYNC, SYNC;
	}

	/**
	 * 测试类型
	 */
	public enum WorkType {

		AG, IC;
	}

	public enum ChnOpt {

		STOP, TEST, PAUSE, RESUME;
	}

	public enum ControlType {

		Fan, ChargePower, AuxiliaryPower, GreenTricolourLight, YellowTricolourLight, RedTricolourLight, Buzzer, GreenPoleLight, YellowPole;

	}

	/**
	 * 主控PC通信协议功能码定义
	 * 
	 * @author Administrator
	 *
	 */
	public enum MainCode implements Code {

		/**
		 * 常量定义
		 */
		DateCode(1), PoleCode(2), BaseCountCode(3), LogicChnSwitchCode(4), ChnOperateCode(5), ProcedureCode(
				6), PickupCode(7), ChnSwitchCode(8), DeviceStateCode(9), AlertCode(0x0A), ModeCode(
						0x0B), SleepProtectCode(0x0C), CCProtectCode(0x0d), CVProtectCode(0x0e), DCProtectCode(
								0x0f), TempControlCode(0x10), DebugControlCode(0x11), DeviceProtectCode(
										0x12), TestNameCode(0x13), ExceptionHandCode(0x16), RemoteUpdateCode(
												0x18), FirstCCProtectCode(0x1A), OfflineUploadCode(
														0x1b), OfflineAcquireCode(0x1f), CheckProtectCode(
																		0x20), PLCProtectCode(0x21),

		PressureChangeCode(0x26), SaveParamCode(0x27), IPAddressCode(0x28), StartEndCheckCode(0x29), EnergyCode(
				0x2A), PushSwitchCode(0x2b), SolenoidValveSwitchCode(0x2c), TrayTempUpperCode(
						0x2d), CylinderPressureCode(0x2e), ProcedureModeCode(0x2f), OfflineRunningCode(
								0x30), BeepAlertCode(0x31), ExChnOperateCode(0x32), ControlUnitCode(0x33),
		AllowStepSkipCode(0x34),UpgradeCode(0x35), UpgradeProgressCode(0x36),versionCode(0x37) ,SelfCheckCode(0x38),
		ResetCode(0x39) , RecoveryCode(0x3A) , DriverChnIndexDefineCode(0x3B) ,ClearSmogCode(0x3C),EnableCheckProtectionCode(0x3D),
		JsonProcedureCode(0x40),JsonProtectionCode(0x41),JsonPoleCode(0x42),JsonDeviceProtectCode(0x43);

		private int code;

		private MainCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static MainCode valueOf(int code) {

			for (MainCode temp : MainCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return null;
		}
	}

	public enum FittingType {

		CHARGE_POWER, AUXILIARY_POWER_SUPPLY, HEAT_FAN, TURBO_FAN;
	}

	public enum FittingRunState {

		CLOSE, RUN;

	}

	public enum FittingWorkState {

		NORMAL, MALFUNCTION;

	}

	public static class FittingData {

		private int id;
		private String cabName;
		private int fittingIndex;
		private FittingType fittingType;
		private FittingRunState runState; // 运行状态
		private FittingWorkState workState; // 故障状态
		private Date date;

		public FittingData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public FittingData(int fittingIndex, FittingType fittingType, FittingRunState runState) {
			super();
			this.fittingIndex = fittingIndex;
			this.fittingType = fittingType;
			this.runState = runState;
			this.date = new Date();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getFittingIndex() {
			return fittingIndex;
		}

		public void setFittingIndex(int fittingIndex) {
			this.fittingIndex = fittingIndex;
		}

		public FittingType getFittingType() {
			return fittingType;
		}

		public void setFittingType(FittingType fittingType) {
			this.fittingType = fittingType;
		}

		public FittingRunState getRunState() {
			return runState;
		}

		public void setRunState(FittingRunState runState) {
			this.runState = runState;
		}

		public FittingWorkState getWorkState() {
			return workState;
		}

		public void setWorkState(FittingWorkState workState) {
			this.workState = workState;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getCabName() {
			return cabName;
		}

		public void setCabName(String cabName) {
			this.cabName = cabName;
		}

	}

	/**
	 * ARM核心板状态对象
	 * 
	 * @author Administrator
	 *
	 */
	public static class CoreData {

		private double memory;
		private double memoryUsed;
		private double memoryFree;

		private double loadAverage5;
		private double loadAverage10;
		private double loadAverage15;

		// 软件版本
		private String version="";
		private State  state=State.NORMAL;
		private String  macAddress = "";
		
		private Date startupDatetime = new Date(); // 系统启动时间
		private Date factoryDatetime = new Date(); //出厂时间
		private long runMiliseconds;  //系统运行累计时间
		private boolean normal;
		
		

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public long getRunMiliseconds() {
			return runMiliseconds;
		}

		public void setRunMiliseconds(long runMiliseconds) {
			this.runMiliseconds = runMiliseconds;
		}

		
		public double getMemory() {
			return memory;
		}

		public void setMemory(double memory) {
			this.memory = memory;
		}

		public double getMemoryUsed() {
			return memoryUsed;
		}

		public void setMemoryUsed(double memoryUsed) {
			this.memoryUsed = memoryUsed;
		}

		public double getLoadAverage5() {
			return loadAverage5;
		}

		public void setLoadAverage5(double loadAverage5) {
			this.loadAverage5 = loadAverage5;
		}

		public double getLoadAverage10() {
			return loadAverage10;
		}

		public void setLoadAverage10(double loadAverage10) {
			this.loadAverage10 = loadAverage10;
		}

		public double getLoadAverage15() {
			return loadAverage15;
		}

		public void setLoadAverage15(double loadAverage15) {
			this.loadAverage15 = loadAverage15;
		}

		public Date getStartupDatetime() {
			return startupDatetime;
		}

		public void setStartupDatetime(Date startupDatetime) {
			this.startupDatetime = startupDatetime;
		}

		public boolean isNormal() {
			return normal;
		}

		public void setNormal(boolean isNormal) {
			this.normal = isNormal;
		}

		public double getMemoryFree() {
			return memoryFree;
		}

		public void setMemoryFree(double memoryFree) {
			this.memoryFree = memoryFree;
		}

		public String getMacAddress() {
			return macAddress;
		}

		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}

		public Date getFactoryDatetime() {
			return factoryDatetime;
		}

		public void setFactoryDatetime(Date factoryDatetime) {
			this.factoryDatetime = factoryDatetime;
		}
		
		

	}
	
	/**
	 * 夹具机监视状态
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
//	public static class FixtureMonitorState {
//		
//		private int   index;  //夹具号
//		private FixtureType  type;
//		private Fixture     fixture;
//		private List<ProbeMonitorData>  tempProbes = new ArrayList<>(); //温度探头
//		private List<ProbeMonitorData>  pressureProbes = new ArrayList<>(); //压力探头
//		
//		
//		public int getIndex() {
//			return index;
//		}
//		public void setIndex(int index) {
//			this.index = index;
//		}
//		public FixtureType getType() {
//			return type;
//		}
//		public void setType(FixtureType type) {
//			this.type = type;
//		}
//		public Fixture getFixture() {
//			return fixture;
//		}
//		public void setFixture(Fixture fixture) {
//			this.fixture = fixture;
//		}
//		public List<ProbeMonitorData> getTempProbes() {
//			return tempProbes;
//		}
//		public void setTempProbes(List<ProbeMonitorData> tempProbes) {
//			this.tempProbes = tempProbes;
//		}
//		public List<ProbeMonitorData> getPressureProbes() {
//			return pressureProbes;
//		}
//		public void setPressureProbes(List<ProbeMonitorData> pressureProbes) {
//			this.pressureProbes = pressureProbes;
//		}
//		
//		
//		
//	}


	/**
	 * 驱动板监视状态
	 * 
	 * @author Administrator
	 *
	 */
//	public static class DriverMonitorState {
//
//		// 驱动板号
//		private int driverIndex;
//		private Logic2Environment.LogicState  state;
//		// 电阻温度1
//		private double resistorTemp1;
//		// 电阻温度2
//		private double resistorTemp2;
//		
//		private boolean use;
//		
//		private String logicSoftversion;
//		private String checkSoftVersion;
//		
//		private String  logicUuid;
//		private String  checkUuid;
//		
//		private boolean logicFlashOk;
//		private boolean checkFlashOk;
//		
//		
//		private boolean logicBurnOk;
//		private boolean checkBurnOk;
//        
//        //累计运行时间,单位ms
//		private long runMiliseconds; 
//		
//		//升级进度
//		private double programProgress = -2.0;
//		
//		
//		
//
//		public double getProgramProgress() {
//			return programProgress;
//		}
//
//		public void setProgramProgress(double programProgress) {
//			this.programProgress = programProgress;
//		}
//
//		public boolean isUse() {
//			return use;
//		}
//
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//
//		public String getLogicUuid() {
//			return logicUuid;
//		}
//
//		public void setLogicUuid(String logicUuid) {
//			this.logicUuid = logicUuid;
//		}
//
//		public String getCheckUuid() {
//			return checkUuid;
//		}
//
//		public void setCheckUuid(String checkUuid) {
//			this.checkUuid = checkUuid;
//		}
//
//		public Logic2Environment.LogicState getState() {
//			return state;
//		}
//
//		public void setState(Logic2Environment.LogicState state) {
//			this.state = state;
//		}
//
//		public long getRunMiliseconds() {
//			return runMiliseconds;
//		}
//
//		public void setRunMiliseconds(long runMiliseconds) {
//			this.runMiliseconds = runMiliseconds;
//		}
//
//		public int getDriverIndex() {
//			return driverIndex;
//		}
//
//		public void setDriverIndex(int driverIndex) {
//			this.driverIndex = driverIndex;
//		}
//
//		public double getResistorTemp1() {
//			return resistorTemp1;
//		}
//
//		public void setResistorTemp1(double resistorTemp1) {
//			this.resistorTemp1 = resistorTemp1;
//		}
//
//		public double getResistorTemp2() {
//			return resistorTemp2;
//		}
//
//		public void setResistorTemp2(double resistorTemp2) {
//			this.resistorTemp2 = resistorTemp2;
//		}
//
//		public String getLogicSoftversion() {
//			return logicSoftversion;
//		}
//
//		public void setLogicSoftversion(String logicSoftversion) {
//			this.logicSoftversion = logicSoftversion;
//		}
//
//		public String getCheckSoftVersion() {
//			return checkSoftVersion;
//		}
//
//		public void setCheckSoftVersion(String checkSoftVersion) {
//			this.checkSoftVersion = checkSoftVersion;
//		}
//
//		public boolean isLogicFlashOk() {
//			return logicFlashOk;
//		}
//
//		public void setLogicFlashOk(boolean logicFlashOk) {
//			this.logicFlashOk = logicFlashOk;
//		}
//
//		public boolean isCheckFlashOk() {
//			return checkFlashOk;
//		}
//
//		public void setCheckFlashOk(boolean checkFlashOk) {
//			this.checkFlashOk = checkFlashOk;
//		}
//
//		public boolean isLogicBurnOk() {
//			return logicBurnOk;
//		}
//
//		public void setLogicBurnOk(boolean logicBurnOk) {
//			this.logicBurnOk = logicBurnOk;
//		}
//
//		public boolean isCheckBurnOk() {
//			return checkBurnOk;
//		}
//
//		public void setCheckBurnOk(boolean checkBurnOk) {
//			this.checkBurnOk = checkBurnOk;
//		}
//
//		
//
//	}

	public enum TrayState {

		NOT_INT_PLACE, IN_PLACE;

	}

	public enum AirPressureState {

		NORMAL, ALERT;

	}

	public enum AlarmState {

		NORMAL, ALERT;
	}
	
	public enum UpgradeType {
		
		Core , CoreCfg , Logic, Check, LogicDriver , CheckDriver;
		
	}
	/**
	 * 自检状态
	 * @author wavy_zheng
	 * 2021年1月18日
	 *
	 */
	public enum SelfCheckState {
		
		NONE , CHECKING , CHECKED;
	}
	
	
	
	/**
	 * 风机监控数据
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
//	public static class FanMonitorData {
//		
//		private int   index;
//		private boolean  working;
//		private boolean  normal;
//		private int      speed;
//		private long     runMiliseconds;
//		private FanType  fanType;
//		private boolean  use;
//		
//		
//		
//		
//		public FanType getFanType() {
//			return fanType;
//		}
//		public void setFanType(FanType fanType) {
//			this.fanType = fanType;
//		}
//		public boolean isUse() {
//			return use;
//		}
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//		public int getIndex() {
//			return index;
//		}
//		public void setIndex(int index) {
//			this.index = index;
//		}
//		public boolean isWorking() {
//			return working;
//		}
//		public void setWorking(boolean working) {
//			this.working = working;
//		}
//		public boolean isNormal() {
//			return normal;
//		}
//		public void setNormal(boolean normal) {
//			this.normal = normal;
//		}
//		public int getSpeed() {
//			return speed;
//		}
//		public void setSpeed(int speed) {
//			this.speed = speed;
//		}
//		public long getRunMiliseconds() {
//			return runMiliseconds;
//		}
//		public void setRunMiliseconds(long runMiliseconds) {
//			this.runMiliseconds = runMiliseconds;
//		}
//		
//		
//	}
	
	/**
	 * 探头检测
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
//	public static class ProbeMonitorData {
//		
//		private int   index;
//		private double value ;   //探头数据
//		private ProbeType  probeType;  //探头类型
//		private boolean  normal; //是否正常
//		private boolean  use;
//		public int getIndex() {
//			return index;
//		}
//		public void setIndex(int index) {
//			this.index = index;
//		}
//		public double getValue() {
//			return value;
//		}
//		public void setValue(double value) {
//			this.value = value;
//		}
//		public ProbeType getProbeType() {
//			return probeType;
//		}
//		public void setProbeType(ProbeType probeType) {
//			this.probeType = probeType;
//		}
//		public boolean isNormal() {
//			return normal;
//		}
//		public void setNormal(boolean normal) {
//			this.normal = normal;
//		}
//		public boolean isUse() {
//			return use;
//		}
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//		
//		
//
//	}
	/**
	 * 控制分区监测模型
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
	public static class ControlUnitMonitorData {
		
		private int   index;
		private State  state;
		private boolean procedure; //是否配置流程
		private boolean protection; //是否配置了保护
		
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public State getState() {
			return state;
		}
		public void setState(State state) {
			this.state = state;
		}
		public boolean isProcedure() {
			return procedure;
		}
		public void setProcedure(boolean procedure) {
			this.procedure = procedure;
		}
		public boolean isProtection() {
			return protection;
		}
		public void setProtection(boolean protection) {
			this.protection = protection;
		}
		
		
	}
	
	
	/**
	 * 气缸检测
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
	public static class CylinderMonitorData {
		
		private int      index;
		private boolean  press; //是否压紧
		private boolean  normal;
		private double   pressure;  //mP
		
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public boolean isPress() {
			return press;
		}
		public void setPress(boolean press) {
			this.press = press;
		}
		public boolean isNormal() {
			return normal;
		}
		public void setNormal(boolean normal) {
			this.normal = normal;
		}
		public double getPressure() {
			return pressure;
		}
		public void setPressure(double pressure) {
			this.pressure = pressure;
		}
		
		
		
	}
	
	/**
	 * 电源状态
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
//	public static class PowerMonitorData {
//		
//		private int        index;
//		private PowerType  powerType;
//		private boolean    working;
//		private boolean    normal;
//		private long       runMiliseconds;
//		private boolean    use;
//		private PowerFaultReasonData   faultInfo;
//		
//		
//		
//		
//		public boolean isUse() {
//			return use;
//		}
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//		public int getIndex() {
//			return index;
//		}
//		public void setIndex(int index) {
//			this.index = index;
//		}
//		public PowerType getPowerType() {
//			return powerType;
//		}
//		public void setPowerType(PowerType powerType) {
//			this.powerType = powerType;
//		}
//		public boolean isWorking() {
//			return working;
//		}
//		public void setWorking(boolean working) {
//			this.working = working;
//		}
//		public boolean isNormal() {
//			return normal;
//		}
//		public void setNormal(boolean normal) {
//			this.normal = normal;
//		}
//		public long getRunMiliseconds() {
//			return runMiliseconds;
//		}
//		public void setRunMiliseconds(long runMiliseconds) {
//			this.runMiliseconds = runMiliseconds;
//		}
//		public PowerFaultReasonData getFaultInfo() {
//			return faultInfo;
//		}
//		public void setFaultInfo(PowerFaultReasonData faultInfo) {
//			this.faultInfo = faultInfo;
//		}
//		
//		
//		
//		
//	}
	
	
	/**
	 * 回检板
	 * @author wavy_zheng
	 * 2020年12月9日
	 *
	 */
//	public static class CheckMonitorData {
//		
//		private int index;
//		private Check2Environment.CheckWorkMode   state ;  //运行状态
//		private long   sendPickupCount; // 发送
//		private long   recvPickupCount; // 采样总次数
//		private String version = "";   //逻辑板软件版本
//		private long   runMiliseconds; //运行时长
//		private boolean use;
//		private boolean commOk;
//		private boolean burnOk;
//		private String identity = "";
//		
//		
//		private Check2FaultCheckData  faultInfo = new Check2FaultCheckData(); //故障信息
//		
//		
//		
//		public String getIdentity() {
//			return identity;
//		}
//		public void setIdentity(String identity) {
//			this.identity = identity;
//		}
//		public int getIndex() {
//			return index;
//		}
//		public void setIndex(int index) {
//			this.index = index;
//		}
//		public Check2Environment.CheckWorkMode getState() {
//			return state;
//		}
//		public void setState(Check2Environment.CheckWorkMode state) {
//			this.state = state;
//		}
//		public long getSendPickupCount() {
//			return sendPickupCount;
//		}
//		public void setSendPickupCount(long sendPickupCount) {
//			this.sendPickupCount = sendPickupCount;
//		}
//		public long getRecvPickupCount() {
//			return recvPickupCount;
//		}
//		public void setRecvPickupCount(long recvPickupCount) {
//			this.recvPickupCount = recvPickupCount;
//		}
//		public String getVersion() {
//			return version;
//		}
//		public void setVersion(String version) {
//			this.version = version;
//		}
//		public long getRunMiliseconds() {
//			return runMiliseconds;
//		}
//		public void setRunMiliseconds(long runMiliseconds) {
//			this.runMiliseconds = runMiliseconds;
//		}
//		public boolean isUse() {
//			return use;
//		}
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//		public Check2FaultCheckData getFaultInfo() {
//			return faultInfo;
//		}
//		public void setFaultInfo(Check2FaultCheckData faultInfo) {
//			this.faultInfo = faultInfo;
//		}
//		public boolean isCommOk() {
//			return commOk;
//		}
//		public void setCommOk(boolean commOk) {
//			this.commOk = commOk;
//		}
//		public boolean isBurnOk() {
//			return burnOk;
//		}
//		public void setBurnOk(boolean burnOk) {
//			this.burnOk = burnOk;
//		}
//		
//		
//		
//	}


	/**
	 * 逻辑板状态数据对象
	 * 
	 * @author Administrator
	 *
	 */
//	public static class LogicMonitorData {
//
//		private int index;
//		private LogicState state = LogicState.UDT;
//		private long   sendPickupCount; // 发送
//		private long   recvPickupCount; // 采样总次数
//		private String version = "";   //逻辑板软件版本
//		private long   runMiliseconds; //运行时长
//		private boolean use;
//		private boolean commOk;
//		private boolean burnOk;
//		private String  identity="";
//		
//		private Logic2FaultCheckData   faultInfo = new Logic2FaultCheckData(); //故障信息
//	
//
//		public LogicMonitorData() {
//
//
//		}
//		
//		
//		
//		public String getIdentity() {
//			return identity;
//		}
//
//
//
//		public void setIdentity(String identity) {
//			this.identity = identity;
//		}
//
//
//
//		public boolean isUse() {
//			return use;
//		}
//
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//
//
//		public boolean isCommOk() {
//			return commOk;
//		}
//
//
//		public void setCommOk(boolean commOk) {
//			this.commOk = commOk;
//		}
//
//
//		public boolean isBurnOk() {
//			return burnOk;
//		}
//
//
//		public void setBurnOk(boolean burnOk) {
//			this.burnOk = burnOk;
//		}
//
//
//		public long getRecvPickupCount() {
//			return recvPickupCount;
//		}
//
//		public void setRecvPickupCount(long pickupCount) {
//			this.recvPickupCount = pickupCount;
//		}
//
//		public int getIndex() {
//			return index;
//		}
//
//		public void setIndex(int index) {
//			this.index = index;
//		}
//
//		
//		public long getSendPickupCount() {
//			return sendPickupCount;
//		}
//
//		public void setSendPickupCount(long sendPickupCount) {
//			this.sendPickupCount = sendPickupCount;
//		}
//
//
//		public String getVersion() {
//			return version;
//		}
//
//
//		public void setVersion(String version) {
//			this.version = version;
//		}
//
//
//		public long getRunMiliseconds() {
//			return runMiliseconds;
//		}
//
//
//		public void setRunMiliseconds(long runMiliseconds) {
//			this.runMiliseconds = runMiliseconds;
//		}
//
//
//		public Logic2FaultCheckData getFaultInfo() {
//			return faultInfo;
//		}
//
//
//		public void setFaultInfo(Logic2FaultCheckData faultInfo) {
//			this.faultInfo = faultInfo;
//		}
//
//
//		public LogicState getState() {
//			return state;
//		}
//
//
//		public void setState(LogicState state) {
//			this.state = state;
//		}
//		
//		
//
//	}
	
	/**
	 * 通道监视状态
	 * @author wavy_zheng
	 * 2021年1月16日
	 *
	 */
	public static class ChannelMonitorData {
		
		private int  index; 
		private ChnState  state; 
		private long   runMiliseconds; //运行时长
		private String  info;   //备注信息
		
		private boolean  logicFlashOk;
		private boolean  checkFlashOk;
		
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public boolean isLogicFlashOk() {
			return logicFlashOk;
		}
		public void setLogicFlashOk(boolean logicFlashOk) {
			this.logicFlashOk = logicFlashOk;
		}
		public boolean isCheckFlashOk() {
			return checkFlashOk;
		}
		public void setCheckFlashOk(boolean checkFlashOk) {
			this.checkFlashOk = checkFlashOk;
		}
		public ChnState getState() {
			return state;
		}
		public void setState(ChnState state) {
			this.state = state;
		}
		public long getRunMiliseconds() {
			return runMiliseconds;
		}
		public void setRunMiliseconds(long runMiliseconds) {
			this.runMiliseconds = runMiliseconds;
		}
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}

		
	}


	/**
	 * 温控板（表）状态对象
	 * 
	 * @author Administrator
	 *
	 */
//	public static class TempMeterMonitorData {
//
//		private int index;
//		private boolean open;
//		private boolean err;
//		private boolean overTempAlert;
//		private double  temperature;
//		private boolean use;
//		private boolean normal; //工作是否正常
//		private TempBoardType meterType; //表类型 
//		
//		
//
//		public boolean isNormal() {
//			return normal;
//		}
//
//		public void setNormal(boolean normal) {
//			this.normal = normal;
//		}
//
//		public boolean isUse() {
//			return use;
//		}
//
//		public void setUse(boolean use) {
//			this.use = use;
//		}
//
//		public int getIndex() {
//			return index;
//		}
//
//		public void setIndex(int index) {
//			this.index = index;
//		}
//
//		public boolean isOpen() {
//			return open;
//		}
//
//		public void setOpen(boolean open) {
//			this.open = open;
//		}
//
//		public boolean isErr() {
//			return err;
//		}
//
//		public void setErr(boolean err) {
//			this.err = err;
//		}
//
//		public boolean isOverTempAlert() {
//			return overTempAlert;
//		}
//
//		public void setOverTempAlert(boolean overTempAlert) {
//			this.overTempAlert = overTempAlert;
//		}
//
//		public double getTemperature() {
//			return temperature;
//		}
//
//		public void setTemperature(double temperature) {
//			this.temperature = temperature;
//		}
//
//		public TempBoardType getMeterType() {
//			return meterType;
//		}
//
//		public void setMeterType(TempBoardType meterType) {
//			this.meterType = meterType;
//		}
//		
//		
//	}

	/**
	 * 通道数据
	 * 
	 * @author wavy_zheng 2020年12月1日
	 *
	 */
	public static class ChannelData implements Cloneable{

		private int id;
		private int unitIndex;  //分区号
		private int channelIndex; // 设备通道序号
		private ChnState state = ChnState.NONE;
		private double voltage; // 当前通道的电池电压 mV
		private double current; // 当前通道的电流mA
		private double capacity; // 当前通道电池容量mAh
		private WorkMode workMode; // 当前工作模式
		private int stepIndex; // 当前测试步次 1开始
		private int loopIndex; // 当前循环步次 1开始
		private long timeStepSpend; // 消耗当前步次时间，单位s
		private long timeTotalSpend;// 消耗当前总流程时间，单位s
		private double temp; // 当前温度
		private double boardTemp; // 夹板温度
		private String barcode; // barcode 序列号
		private Date date; // 当前保存时间
		private double energy; // 能量
		private double powerVoltage; // 功率电压
		private double deviceVoltage; // 设备电压
		private boolean importantData; // 重要转点数据

		private int tableIndex; // 查询数据时表格序号
		private AlertCode alertCode = AlertCode.NORMAL; // 报警码
		private double alertVoltage = 0; // 报警电压mV
		private double alertCurrent = 0; // 报警电压mV

		private double accumulateCapacity;
		private double accumulateEnergy;
		private boolean syncMode; //进入同步模式？

		private double pressure; // 压力
		private Map<String, Object> dataMap = new HashMap<String, Object>(); // 携带数据

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getChannelIndex() {
			return channelIndex;
		}

		public void setChannelIndex(int channelIndex) {
			this.channelIndex = channelIndex;
		}

		public ChnState getState() {
			return state;
		}

		public void setState(ChnState state) {
			this.state = state;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public double getVoltage() {
			return voltage;
		}

		public void setVoltage(double voltage) {
			this.voltage = voltage;
		}

		public double getCurrent() {
			return current;
		}

		public void setCurrent(double current) {
			this.current = current;
		}

		public double getCapacity() {
			return capacity;
		}

		public void setCapacity(double capacity) {
			this.capacity = capacity;
		}

		public WorkMode getWorkMode() {
			return workMode;
		}

		public void setWorkMode(WorkMode workMode) {
			this.workMode = workMode;
		}

		public int getStepIndex() {
			return stepIndex;
		}

		public void setStepIndex(int stepIndex) {
			this.stepIndex = stepIndex;
		}

		public int getLoopIndex() {
			return loopIndex;
		}

		public void setLoopIndex(int loopIndex) {
			this.loopIndex = loopIndex;
		}

		public long getTimeStepSpend() {
			return timeStepSpend;
		}

		public void setTimeStepSpend(long timeStepSpend) {
			this.timeStepSpend = timeStepSpend;
		}

		public long getTimeTotalSpend() {
			return timeTotalSpend;
		}

		public void setTimeTotalSpend(long timeTotalSpend) {
			this.timeTotalSpend = timeTotalSpend;
		}

		public double getTemp() {
			return temp;
		}

		public void setTemp(double temp) {
			this.temp = temp;
		}

		public boolean isRunning() {

			return state == ChnState.RUN;
		}

		public String getBarcode() {
			return barcode;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}

		public int getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(int tableIndex) {
			this.tableIndex = tableIndex;
		}

		/**
		 * 将chnData里的电压(V) * 容量(mAh) 得到能量(mWh)
		 * 
		 * @param chnData
		 * @return
		 */
		public static double calculateEnergy(ChannelData chnData) {

			double capacity = (double) chnData.getCapacity() / 10; // mAh
			double voltage = (double) chnData.getVoltage() / 10000; // V
			return capacity * voltage;
		}

		public AlertCode getAlertCode() {
			return alertCode;
		}

		public void setAlertCode(AlertCode alertCode) {
			this.alertCode = alertCode;
		}

		public double getAlertVoltage() {
			return alertVoltage;
		}

		public void setAlertVoltage(double alertVoltage) {
			this.alertVoltage = alertVoltage;
		}

		public double getAlertCurrent() {
			return alertCurrent;
		}

		public void setAlertCurrent(double alertCurrent) {
			this.alertCurrent = alertCurrent;
		}

		public double getPressure() {
			return pressure;
		}

		public void setPressure(double pressure) {
			this.pressure = pressure;
		}

		public double getEnergy() {
			return energy;
		}

		public void setEnergy(double energy) {
			this.energy = energy;
		}

		public double getPowerVoltage() {
			return powerVoltage;
		}

		public void setPowerVoltage(double powerVoltage) {
			this.powerVoltage = powerVoltage;
		}

		public double getDeviceVoltage() {
			return deviceVoltage;
		}

		public void setDeviceVoltage(double deviceVoltage) {
			this.deviceVoltage = deviceVoltage;
		}

		public Object getData() {
			return dataMap.get("data");
		}

		public void setData(Object data) {
			dataMap.put("data", data);
		}

		public void putData(String key, Object val) {

			dataMap.put(key, val);
		}

		public Object getDataFromKey(String key) {

			return dataMap.get(key);
		}

		public double getBoardTemp() {
			return boardTemp;
		}

		public void setBoardTemp(double boardTemp) {
			this.boardTemp = boardTemp;
		}

		public boolean isImportantData() {
			return importantData;
		}

		public void setImportantData(boolean importantData) {
			this.importantData = importantData;
		}

		public double getAccumulateCapacity() {
			return accumulateCapacity;
		}

		public void setAccumulateCapacity(double accumulateCapacity) {
			this.accumulateCapacity = accumulateCapacity;
		}

		public double getAccumulateEnergy() {
			return accumulateEnergy;
		}

		public void setAccumulateEnergy(double accumulateEnergy) {
			this.accumulateEnergy = accumulateEnergy;
		}

		public int getUnitIndex() {
			return unitIndex;
		}

		public void setUnitIndex(int unitIndex) {
			this.unitIndex = unitIndex;
		}

		public boolean isSyncMode() {
			return syncMode;
		}

		public void setSyncMode(boolean syncMode) {
			this.syncMode = syncMode;
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
		
		
		

		@Override
		public String toString() {
			return "ChannelData [id=" + id + ", channelIndex=" + channelIndex + ", state=" + state + ", voltage="
					+ voltage + ", current=" + current + ", capacity=" + capacity + ", workMode=" + workMode
					+ ", stepIndex=" + stepIndex + ", loopIndex=" + loopIndex + ", timeStepSpend=" + timeStepSpend
					+ ", timeTotalSpend=" + timeTotalSpend + ", temp=" + temp + ", boardTemp=" + boardTemp
					+ ", barcode=" + barcode + ", date=" + date + ", energy=" + energy + ", powerVoltage="
					+ powerVoltage + ", deviceVoltage=" + deviceVoltage + ", importantData=" + importantData
					+ ", tableIndex=" + tableIndex + ", alertCode=" + alertCode + ", alertVoltage=" + alertVoltage
					+ ", alertCurrent=" + alertCurrent + ", accumulateCapacity=" + accumulateCapacity
					+ ", accumulateEnergy=" + accumulateEnergy + ", pressure=" + pressure + "]";
		}

	}

}
