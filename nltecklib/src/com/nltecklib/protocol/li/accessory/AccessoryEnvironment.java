package com.nltecklib.protocol.li.accessory;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * 插件环境变量
 * 
 * @author Administrator
 *
 */
public class AccessoryEnvironment {

	/**
	 * 设备在恒温系统关闭后允许的最高温度上限;如果超温将不允许启动流程;如流程已启动则触发超温报警
	 */
	public final static double CONTROL_OFF_TEMP_UPPER = 40;

	public enum AccessoryCode implements Code {
		PowerStateCode(0x01), PowerSwitchCode(0x02), FanStateCode(0x05), OMRMeterSwitchCode(0x06),
		OMRMeterTempCode(0x07), MeterStateCode(0x08), SmogAlertCode(0x09), ORMMeterTempUpperCode(0x0A),
		LightAudioCode(0x0B), NLTempCode(0x0C), TempQueryCode(0x0D), PoleLightCode(0x0E), FanControlCode(0x0F),
		OMRTempModeCode(0x10), TurboFanCode(0x11), ValveSwitchCode(0x12), ValveTempCode(0x13),
		MechanismStateQueryCode(0x14), PressureCode(0x15), AnotherFanStateCode(0x16), TempProbeCode(0x17),
		DoorCode(0x18),HeatPipeStatusCode(0x19),HeartBeatCode(0x20),PowerFaultReasonCode(0x21),
		ColorLightCode(0x22),BeepCode(0x23),POWER_STATE(0x24),POWER_SWITCH2(0x25),FAN_STATE(0x26),FAN_CONTROL2(0x27),
		HEART_BEAT2(0x28),IndicatorCode(0x29),ADDRESS(0x30),EmergencyStopCode(0x31),PowerResetCode(0x32),SelectLightCode(0x33),
		PowerSupplyCode(0x34),PowerErrorInfoCode(0x35),PowerLLZErrorInfoCode(0x36),ChannelLightCode(0x37),PingStateCode(0x38),
		FourLightStateCode(0x39),AirPressureStateCode(0x3A),AirValveSwitchCode(0x3B),PingCalibrateSwitchCode(0x3C);

		private int code;

		private AccessoryCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {

			return code;
		}

		public static AccessoryCode valueOf(int code) {

			
			for (AccessoryCode temp : AccessoryCode.values()) {
				if(temp.getCode()==code){
					return temp;
				}
			}
			
			return null;
		}

	}

	/**
	 * 报警器类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum AlarmType {

		/**
		 * AUDIO_LIGHT : 传统的三色灯蜂鸣器报警器 SMOG : 烟雾报警器 POLE : 极性灯报警器
		 */
		AUDIO_LIGHT, SMOG, POLE;

	}

	public enum PowerType {

		/**
		 * CHARGE : 充放电源 AUXILIARY: 辅助电源
		 */
		CHARGE, AUXILIARY;

	}

	public enum FanType {

		/*
		 * COOL 为散热风机 ; TURBO 为涡轮风机
		 */
		COOL, TURBO;
	}

	public enum TempBoardType {

		OMR /* 欧姆容 */ , OMR_PROTECT /* 欧姆容保护表，副表 */;
	}

	/**
	 * 开关状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum PowerState {

		OFF, ON;
	}

	/**
	 * 阀开关
	 * 
	 * @author Administrator
	 *
	 */
	public enum ValveState {

		OPEN/* 打开 */, CLOSE/* 闭合 */ , EXCEPT/* 异常 */
	}

	public enum AlertState {

		NORMAL, ALERT;
	}

	/**
	 * 工作状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum WorkState {

		NORMAL, FAULT;
	}

	public enum Direction {

		IN, OUT;
	}

	/**
	 * 超温报警标志
	 * 
	 * @author Administrator
	 *
	 */
	public enum OverTempFlag {

		NORMAL, ALERT
	}

	/**
	 * 恒温加热模式
	 * 
	 * @author Administrator
	 *
	 */
	public enum HeatMode {

		AT, ST;
	}

	/**
	 * 加热丝状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum HeatLine {

		NORMAL, BREAKDOWN;
	}

	/**
	 * 温控表电流
	 * 
	 * @author Administrator
	 *
	 */
	public enum HeatMeterCurrent {

		NORMAL, OVER;
	}

	/**
	 * 托盘温度探头对象
	 * 
	 * @author Administrator
	 *
	 */
	public static class TrayTempProbe {

		public double temperature;
		public OverTempFlag state = OverTempFlag.NORMAL;
	}
	
	/**
	 * 探头类型，暂时只支持温度
	 * @author Administrator
	 *
	 */
	public enum ProbeType {
		
		Temperature , Door , Smog , Pressure
	}
}
