package com.nltecklib.protocol.lab.main;

import com.nltecklib.protocol.lab.Environment.Code;

public class MainEnvironment {

	public final static int MAX_COREBOARD_CHN_COUNT = 8;

	/**
	 * 报警码
	 * 
	 * @author Administrator
	 *
	 */
	public enum AlertCode {

		NORMAL, VOLT_UPPER, VOLT_LOWER, CUR_UPPER, CUR_LOWER, CAPACITY_UPPER, TIME_OVER, TEMP_OVER, POLE_REVERSE,
		OFFPOWER, VOLT_WAVE, CURR_WAVE, DEVICE_ERROR, COMM_ERROR, OFFLINE, TOUCH, LOGIC;

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
			case VOLT_WAVE:
				return "电压波动";
			case CURR_WAVE:
				return "电流波动";
			case DEVICE_ERROR:
				return "硬件故障";
			case COMM_ERROR:
				return "通信故障";
			case OFFLINE:
				return "设备掉线";
			case TOUCH:
				return "接触保护";
//			case LOGIC:
//				return "逻辑错误";
			}

			return "";
		}
	}

	public static class CalDot {

		public CalMode workMode;
		public int precision;
		public double meter; // 表值
		public double adc;
		public double adcK;
		public double adcB;
		public long da;
		public double programK;
		public double programB;

	}

	public static class CalBackUpDot {
		public CalMode workMode;
		public double adc;
		public double adcK;
		public double adcB;

	}

	public enum CalMode {
		SLEEP, CC, CV, DC, DV
	}

	public enum CalBackupMode {

		SLEEP, CV;
	}

	public enum ErrCode {

		NORMAL, OVER_CHARGE, OVER_DISCHARGE, OVER_VOLT, REVERSE_VOLT, OVER_TEMP
	}

	/**
	 * 设备或分区状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum OptState {

		NORMAL, FORMATION, PAUSE, STOP, MAINTAIN, CAL, ALERT, CLOSE, COMPLETE, STARTUP, UPGRADE;

		/**
		 * 是否处于准备测试状态
		 * 
		 * @return
		 */
		public boolean isReady() {

			if (this == OptState.CAL || this == OptState.MAINTAIN || this == OptState.CLOSE || this == OptState.ALERT)
				return false;
			return true;
		}

		// 能否暂停
		public boolean canPause() {

			return this == OptState.FORMATION;
		}

		public boolean canResume() {

			return this == OptState.PAUSE || this == OptState.ALERT;
		}

		// 能否启动测试
		public boolean canStartup() {

			return this == OptState.NORMAL || this == OptState.COMPLETE || this == OptState.STOP;
		}

		// 能否停止测试
		public boolean canStop() {

			return this == OptState.FORMATION || this == OptState.PAUSE || this == OptState.STARTUP;
		}

		public static OptState fromChnState(ChnState cs) {

			OptState state = null;
			switch (cs) {
			case NONE:
				state = OptState.NORMAL;
				break;
			case COMPLETE:
				state = OptState.COMPLETE;
				break;
			case CLOSE:
				state = OptState.CLOSE;
				break;
			case ALERT:
				state = OptState.ALERT;
				break;
			case PAUSE:
				state = OptState.PAUSE;
				break;
			case RUN:
				state = OptState.FORMATION;
				break;
			case STOP:
				state = OptState.STOP;
				break;
			case UDT:
				state = OptState.NORMAL;
				break;
			case UPGRADE:
				state = OptState.UPGRADE;
				break;
			case CAL:
				state = OptState.CAL;
				break;
			}

			return state;
		}

		@Override
		public String toString() {

			String str = "";
			switch (this) {

			case NORMAL:
				str = "待测试";
				break;
			case FORMATION:
				str = "运行";
				break;
			case CLOSE:
				str = "关闭";
				break;
			case MAINTAIN:
				str = "维修";
				break;
			case PAUSE:
				str = "暂停";
				break;
			case STOP:
				str = "停止";
				break;
			case ALERT:
				str = "异常";
				break;
			case CAL:
				str = "校准";
				break;
			case COMPLETE:
				str = "完毕";
				break;
			case STARTUP:
				str = "启动中";
				break;
			case UPGRADE:
				str = "升级";
				break;
			}

			return str;
		}
	}

	/**
	 * 通道状态 0:无电池 1:待测试 2:测试中 3:暂停 4:停止 5:关闭 6:保护 7:完成 8:升级中 9:校准
	 * 
	 * @author Administrator
	 *
	 */
	public enum ChnState {

		NONE, UDT, RUN, PAUSE, STOP, CLOSE, ALERT, COMPLETE, UPGRADE, CAL;
	}

	/**
	 * 工作模式
	 * 
	 * @author Administrator
	 *
	 */
	public enum WorkMode {

		SLEEP, CCC, CVC, CPC, CRC, CDC, CDV, CDP, CDR, PAUSE , CCCV , DCDV , PULSE /*, CBL, CAL */;

		@Override
		public String toString() {
			switch (this) {

			case SLEEP:
				return "Rest";
			case CCC:
				return "CC";
			case CVC:
				return "CV";
			case CPC:
				return "CW";
			case CRC:
				return "CR";
			case CDC:
				return "DC";
			case CDV:
				return "DV";
			case CDP:
				return "DW";
			case CDR:
				return "DR";
			case PAUSE:
				return "Pause";
			case DCDV:
				return "DCDV";
			case CCCV:
				return "CCCV";
			case PULSE:
				return "PULSE";
			
			}

			return "";
		}
		
		/**
		 * 充电模式
		 * @author  wavy_zheng
		 * 2021年10月20日
		 * @param wm
		 * @return
		 */
		public static boolean isChargeMode(WorkMode wm) {
			
			if(wm == WorkMode.CCC || wm == WorkMode.CVC || wm == WorkMode.CPC || wm == WorkMode.CRC ||
					wm == WorkMode.CCCV) {
				
				return true;
			}
			
			
			return false;
			
		}
		
		
		public static boolean isDischargeMode(WorkMode wm) {
			
			if(wm == WorkMode.CDC || wm == WorkMode.CDV || wm == WorkMode.CDP || wm == WorkMode.CDR ||
					wm == WorkMode.DCDV) {
				
				return true;
			}
			
			
			return false;
			
		}
		
		/**
		 * 使用汉字打印工作模式
		 * @author  wavy_zheng
		 * 2021年11月2日
		 * @param wm
		 * @return
		 */
		public static String printWorkModeChineseStr(WorkMode wm) {
			
			if (wm == null) {

				return "--";
			}
			switch (wm) {
			case SLEEP:
				return "搁置";
			case CCC:
				return "恒流充电";
			case CVC:
				return "恒压充电";
			case CPC:
				return "恒功率充电";
			case CRC:
				return "恒阻充电";
			case CDC:
				return "恒流放电";
			case CDV:
				return "恒压放电";
			case CDP:
				return "恒功率放电";
			case CDR:
				return "恒阻放电";
			case PAUSE:
				return "暂停";
			case DCDV:
				return "恒流恒压放电";
			case CCCV:
				return "恒流恒压充电";
			
			}
			return "搁置";
			
		}
		
		public static String printWorkmode(WorkMode wm) {

			if (wm == null) {

				return "--";
			}
			switch (wm) {
			case SLEEP:
				return "Rest";
			case CCC:
				return "CC";
			case CVC:
				return "CV";
			case CPC:
				return "CW";
			case CRC:
				return "CR";
			case CDC:
				return "DC";
			case CDV:
				return "DV";
			case CDP:
				return "DW";
			case CDR:
				return "DR";
			case PAUSE:
				return "Pause";
			case DCDV:
				return "DCDV";
			case CCCV:
				return "CCCV";
			
			}
			return "Rest";
		}
		
		
		public static WorkMode  convertFrom(String text) {
			
			for(WorkMode wm : WorkMode.values()) {
				
				if(WorkMode.printWorkmode(wm).equals(text) || 
						WorkMode.printWorkModeChineseStr(wm).equals(text)) {
					
					return wm;
				}
				
			}
			
			return null;
		}
	}
	
	

	

	/**
	 * 保护类型(保护参数分开下发)
	 */
	public enum ProtectionType {
		GENERAL /* 通道一级保护 */, TOUCH, POLE, SLEEP, CC, CV, CP, CR, DC, DV, DP, DR;
	}

	/**
	 * 时间步次结束模式，继续和终止
	 */
	public enum OverMode {

		PROCEED,  END;
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
		ProcedureCode(0x01), PoleCode(0x02), InitCheckCode(0x03), ChnSwitchCode(0x04), ChnOperateCode(0x05),
		AssistCode(0x06), PickupCode(0x07), PickupExCode(0x09), ChannelAlertCode(0x0A), ChnFirstLevelProtectCode(0x0B),
		SleepProtectCode(0x0C), CCProtectCode(0x0d), CVProtectCode(0x0e), DCProtectCode(0x0f), CPProtectCode(0x10),
		DPProtectCode(0x11), CRProtectCode(0x12), DRProtectCode(0x13), CPSProtectCode(0x14), DPSProtectCode(0x15),
		DeviceExceptionCode(0x16), TouchProtectCode(0x17), IPAddressCode(0x18), DateCode(0x19), UpgradeCode(0x1A),
		DVProtectCode(0x1b), JsonCode(0x1c) , UpgradeProgressCode(0x1d),SoftVersionCode(0x1e),HeartBeatCode(0x1f),
		TestnameCode(0x20),SkipStepCode(0x21),ResetCode(0x22),EnvProtectCode(0x23),CCVProtectCode(0x24),CDVProtectCode(0x25),
		OfflineCfgCode(0x26),OfflineDataCode(0x27),UnitManageCode(0x28) , UnitTitleCode(0x29) , LoopCapacityCode(0x2A),
		TestResultCode(0x2b), SecurityProtectCode(0x2c),UnitStateCode(0x2d),SoftVersionExCode(0x2e),UnitConnectCode(0x2f),
		UnitConnectInfoCode(0x30),UnitAddressCode(0x31) , FileTransCode(0x32) , SnapShotCode(0x33) , CcCvProtectCode(0x34),
		DcDvProtectCode(0x35) , PoleExCode(0x36), GeneralCode(0x37),
		

		// 校准部分
		CalChnVoltageCurrentCode(0x51), CalQueryChnADCCode(0x52), CalBackupVoltageCode(0x53), CalChnEnableCode(0x54),
		CalChnMeterCode(0x55), CalSaveChnFlashCode(0x56), CalSaveChnBackupVoltageFlashCode(0x57),
		CalChnBaseVoltageCode(0x58),CalBackupVoltageMeterCode(0x59) , CalQueryChnADCExCode(0x5A) , CalChnMeterExCode(0x5B),
		CalSaveChnFlashExCode(0x5C),

		;

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
				if(temp.getCode()==code){
					return temp;
				}
			}

//			switch (code) {
//
//			case 0x01:
//				return ProcedureCode;
//			case 0x02:
//				return PoleCode;
//			case 0x03:
//				return InitCheckCode;
//			case 0x04:
//				return ChnSwitchCode;
//			case 0x05:
//				return ChnOperateCode;
//			case 0x06:
//				return AssistCode;
//			case 0x07:
//				return PickupCode;
//			case 0x0A:
//				return ChannelAlertCode;
//			case 0x0B:
//				return ChnFirstLevelProtectCode;
//			case 0x0C:
//				return SleepProtectCode;
//			case 0x0d:
//				return CCProtectCode;
//			case 0x0e:
//				return CVProtectCode;
//			case 0x0f:
//				return DCProtectCode;
//			case 0x10:
//				return CPProtectCode;
//			case 0x11:
//				return DPProtectCode;
//			case 0x12:
//				return CRProtectCode;
//			case 0x13:
//				return DRProtectCode;
//			case 0x14:
//				return CPSProtectCode;
//			case 0x15:
//				return DPSProtectCode;
//			case 0x16:
//				return DeviceExceptionCode;
//			case 0x17:
//				return TouchProtectCode;
//			case 0x18:
//				return IPAddressCode;
//			case 0x19:
//				return DateCode;
//			case 0x1A:
//				return UpgradeCode;
//			case 0x1b:
//				return DVProtectCode;
//			case 0x1c:
//				return JsonCode;
//
//			case 0x51:
//				return CalChnVoltageCurrentCode;
//			case 0x52:
//				return CalQueryChnADCCode;
//			case 0x53:
//				return CalBackupVoltageCode;
//			case 0x54:
//				return CalChnEnableCode;
//			case 0x55:
//				return CalChnMeterCode;
//			case 0x56:
//				return CalSaveChnFlashCode;
//			case 0x57:
//				return CalSaveChnBackupVoltageFlashCode;
//			case 0x58:
//				return CalChnBaseVoltageCode;
//			}

			return null;
		}
	}

	public enum DeviceOpt {

		STARTUP, PAUSE, STOP, RESUME;
	}

	/**
	 * 轻微程度报警；设备不做任何处理 中等程度报警：设备做报警提醒（声光报警） 严重程度报警: 设备做出暂停流程处理 最高等级报警: 设备做出关闭电源等操作
	 */
	public enum AlertGrade {

		SLIGHT, MID, SEVERE, HIGHEST

	}

	/**
	 * 分选结果
	 * 
	 * @author Administrator
	 *
	 */
	public enum CapacityFilterResult {

		NONE, UNFILTER, GOOD, CAPACITY_BAD, STEP_BAD, VOLT_BAD;

		@Override
		public String toString() {

			String text = "";
			switch (this) {
			case NONE:
				text = "无电芯";
				break;
			case UNFILTER:
				text = "未分选";
				break;
			case GOOD:
				text = "良品";
				break;
			case CAPACITY_BAD:
				text = "容量坏品";
				break;
			case STEP_BAD:
				text = "步次坏品";
				break;
			case VOLT_BAD:
				text = "电压坏品";
				break;
			}
			return text;
		}
	}

	public enum AirPressureState {

		NORMAL, ALERT;

		public String toString() {

			switch (this) {
			case ALERT:
				return "异常";
			case NORMAL:
				return "正常";
			}
			return "";
		}
	}

	public enum ConnectState {

		CONNECT, DISCONNECT;

		public String toString() {

			switch (this) {
			case DISCONNECT:
				return "已断开";
			case CONNECT:
				return "已连接";
			}
			return "";
		}
	}

	public enum AlarmState {

		NORMAL, ALERT;

		public String toString() {

			switch (this) {
			case ALERT:
				return "报警";
			case NORMAL:
				return "正常";
			}
			return "";
		}
	}

	/**
	 * 通道操作类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum ChnOptType {

		STOP, START, PAUSE, RESUME, CALIBRATE, LOCK, UNLOCK , STOP_CAL;
	}

	/**
	 * 断电恢复 0:自动运行掉电前的流程, 1:需要点击恢复运行, 2:停止流程
	 * 
	 * @author Administrator
	 */
	public enum Recover {
		AUTO, CLICK, STOP;
	}

	public enum Pole {

		REVERSE, NORMAL;
	}

	/**
	 * 升级的程序类型 0：核心主控程序, 1:核心采集板程序, 2:备份采集板程序 ,3主控配置文件, 4模片
	 * 
	 * @author Administrator
	 *
	 */
	public enum UpgradeType {

		MAIN, CORECHIP, BACKUPCHIP, MAINCFG , MODULE;
		
		@Override
		public String toString() {
			
			switch(this) {
			
			case MAIN:
				return "主控程序";
			case CORECHIP:
				return "采集芯片";
			case BACKUPCHIP:
				return "备份芯片";
			case MAINCFG:
				return "主控配置";
			case MODULE:
				return "模片";
			}
			
			return "";
		}
	}

	/**
	 * Json传输的类型 0：流程, 1：保护参数, 2：主控异常处理方式 , 3测试名
	 * 
	 * @author Administrator
	 *
	 */
	public enum JsonContentType {

		PROCEDURE, PROTECTIONPARAM, EXCEPTION,TESTNAME;
	}

	/**
	 * 通道属性
	 */
	public enum ChnAttribute {

		READONLY /* 只读 */;
	}
	/**
	 * 关键点保存标记
	 * @author wavy_zheng
	 * 2020年6月12日
	 *
	 */
	public enum SaveFlag {
		
		COMMON , IMPORT;
	}
	
	
	public enum VoltageUnit {
		
		mV , V;
		
	}
	
	public enum CurrentUnit {
		
		mA , A;
	}
	
	
	public enum CapacityUnit {
		
		mAh , Ah;
	}
	
	public enum EnergyUnit {
		
		mWh , Wh;
	}
	
	public enum PowerUnit {
		
		mW , W;
	}
	
	public enum ResisterUnit {
		
		
		mR , R ;
		
		@Override
		public String toString() {
			
			String str = "";
			switch(this) {
			
			case mR:
				str = "mΩ";
				break;
			case R:
				str = "Ω";
				break;
			
			}
			
			return str;
			
		}
	}
	
	public enum TimeUnit {
		
		ms , s;
	}

}
