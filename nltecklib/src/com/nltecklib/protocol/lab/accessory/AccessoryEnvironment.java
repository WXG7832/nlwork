package com.nltecklib.protocol.lab.accessory;

import com.nltecklib.protocol.lab.Environment.Code;

/**
 * 控制板环境
 * 
 * @author caichao_tang
 *
 */
public class AccessoryEnvironment {

	/**
	 * 设备在恒温系统关闭后允许的最高温度上限;如果超温将不允许启动流程;如流程已启动则触发超温报警
	 */
	public final static double CONTROL_OFF_TEMP_UPPER = 40;

	public final static int OMR_TEMP_CONSTANT_INDEX = 0x00; // 一号表通常控制恒温
	public final static int OMR_TEMP_ALERT_INDEX = 0x01; // 二号表通常控制报警

	/**
	 * 控制板功能码枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum AccessoryCode implements Code {

		PowerStateCode(0x01), PowerSwitchCode(0x02), FanStateCode(0x03), OMRMeterSwitchCode(0x04), OMRMeterTempCode(
				0x05), OMRMeterStateCode(0x06), ORMMeterTempUpperCode(0x07), ThreeColorLightCode(0x08), TempQueryCode(
						0x09), CoolFanCode(0x0A), TurboFanCode(0x0B), TempProbeCode(0x0C), ChnLightCode(
								0x0D), BuzzerCode(0x0E), HeatDuctSwitchCode(0x0F), SmogAlertCode(0x10), HeartBeatCode(
										0x11), IPAddressCode(0x12), CHN_LIGHT_BATCH_CONTROL(0x13), ScreenCommStateCode(
												0x14), ScreenUnitIPCode(0x15), ScreenUnitStateCode(
														0x16), ScreenControlStateCode(0x17), DeviceOptBtnCode(
																0x18), ChannelLightCode(0x19), PolarLightCode(
																		0x1A), IPC_PowerCode(0x1B);

		private int code;

		private AccessoryCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static AccessoryCode valueOf(int code) {

			switch (code) {

			case 0x01:
				return PowerStateCode;
			case 0x02:
				return PowerSwitchCode;
			case 0x03:
				return FanStateCode;
			case 0x04:
				return OMRMeterSwitchCode;
			case 0x05:
				return OMRMeterTempCode;
			case 0x06:
				return OMRMeterStateCode;
			case 0x07:
				return ORMMeterTempUpperCode;
			case 0x08:
				return ThreeColorLightCode;
			case 0x09:
				return TempQueryCode;
			case 0x0A:
				return CoolFanCode;
			case 0x0B:
				return TurboFanCode;
			case 0x0C:
				return TempProbeCode;
			case 0x0D:
				return ChnLightCode;
			case 0x0E:
				return BuzzerCode;
			case 0x0F:
				return HeatDuctSwitchCode;
			case 0x10:
				return SmogAlertCode;
			case 0x11:
				return HeartBeatCode;
			case 0x12:
				return IPAddressCode;
			case 0x13:
				return CHN_LIGHT_BATCH_CONTROL;
			case 0x14:
				return ScreenCommStateCode;
			case 0x15:
				return ScreenUnitIPCode;
			case 0x16:
				return ScreenUnitStateCode;
			case 0x17:
				return ScreenControlStateCode;
			case 0x18:
				return DeviceOptBtnCode;
			case 0x19:
				return ChannelLightCode;
			case 0x1A:
				return PolarLightCode;
			case 0x1B:
				return IPC_PowerCode;
			}
			return null;
		}
	}

	/**
	 * 电源类型枚举对象
	 */
	public enum PowerType {

		CHARGE, AUXILIARY;

		@Override
		public String toString() {
			switch (this) {
			case CHARGE:
				return "充放电源";
			case AUXILIARY:
				return "辅助电源";
			}

			return "";
		}
	}

	/**
	 * 风扇类型枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum FanType {

		COOL, TURBO;

		@Override
		public String toString() {
			switch (this) {
			case COOL:
				return "散热风机";
			case TURBO:
				return "涡轮风机";
			}

			return "";
		}
	}

	/**
	 * 热风扇类型
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum HeatFanType {

		IN, OUT;

		@Override
		public String toString() {
			switch (this) {
			case IN:
				return "进风风扇";
			case OUT:
				return "出风风扇";
			}

			return "";
		}
	}

	public enum RunState {

		OFF, ON;

		@Override
		public String toString() {
			switch (this) {
			case OFF:
				return "关闭";
			case ON:
				return "运行";
			}

			return "";
		}
	}

	public enum WorkState {

		NORMAL, FAULT;

		@Override
		public String toString() {
			switch (this) {
			case NORMAL:
				return "正常";
			case FAULT:
				return "故障";
			}

			return "";
		}
	}

	// public enum PowerState {
	//
	// /**
	// * 开关状态
	// */
	// OFF, ON;
	// }

	public enum TempBoardType {

		/**
		 * OMR:欧姆容, OMR_PROTECT:欧姆容保护表，副表
		 */
		OMR, OMR_PROTECT;
	}

	public enum TemperatureState {

		NORMAL, OVERTEMPERATURE;

		public String toString() {

			switch (this) {
			case NORMAL:
				return "正常";
			case OVERTEMPERATURE:
				return "超温";
			}
			return "";
		}
	}

	public enum AlertState {

		NORMAL, ALERT;
	}

	public enum Direction {

		IN, OUT;
	}

	public enum OverTempFlag {

		/**
		 * 超温报警标志
		 */
		NORMAL, ALERT
	}

	/**
	 * byte和bit的转换
	 */
	private final static int BIT_SIZE = 8;

	public static int[] byteToBit(byte b) {

		int[] bit = new int[BIT_SIZE];
		for (int j = 0; j < bit.length; j++) {

			// bit[8 - j - 1] = (b >> j) & 1;
			bit[j] = (b >> j) & 1;
		}
		return bit;
	}

	public static boolean[] byteToBoolean(byte b) {

		boolean[] bit = new boolean[BIT_SIZE];
		for (int j = 0; j < bit.length; j++) {

			bit[j] = ((b >> j) & 1) == 1;
		}
		return bit;
	}

	public static byte BooleanToByte(boolean[] bit) {

		if (bit != null && bit.length > 0) {
			byte b = 0;
			for (int i = 0; i < BIT_SIZE; i++) {

				if (bit[i]) {

					int n = (1 << i);
					b += n;
				}
			}
			return b;
		}
		return 0;
	}

}
