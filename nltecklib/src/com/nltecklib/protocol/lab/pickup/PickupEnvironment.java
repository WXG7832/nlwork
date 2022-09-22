package com.nltecklib.protocol.lab.pickup;

import com.nltecklib.protocol.lab.Environment.Code;

/**
 * 采集板协议环境
 * 
 * @author Administrator
 *
 */
public class PickupEnvironment {

	public enum ChipPickupCode implements Code {

		/**
		 * 功能码常量定义
		 */
		WorkEnvCode(0x01), StepCode(0x02), OptCode(0x03), PickupCode(0x04), ProtectCode(0x05), CalibrateCode(
				0x06), CalculateCode(0x07), ModuleEnableCode(0x08), FlashParamsCode(0x09), UpgradeCode(
						0x0A), ConnectCode(0x0B), VoltBoundaryCode(0x0C), InfoCode(0x0D), HardErrInfoCode(
								0x0E), PulseCode(0x10), InfoExCode(0x11), PickupExCode(0x12), CalibrateExCode(
										0x13), CalculateExCode(0x14), FlashParamsExCode(0x15), IPAddressCode(
												0x16), VoltBoundaryExCode(
														0x19), SoftInfoCode(0x20), MultiModuleFlashParamsCode(
																0x21), MultiModuleCalibrateCode(
																		0x22), MultiModuleCalculateCode(
																				0x23), DriverSelfCheckCode(0x24);

		private int code;

		private ChipPickupCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {

			return code;
		}

		public static ChipPickupCode valueOf(int code) {

			switch (code) {

			case 0x01:
				return WorkEnvCode;
			case 0x02:
				return StepCode;
			case 0x03:
				return OptCode;
			case 0x04:
				return PickupCode;
			case 0x05:
				return ProtectCode;
			case 0x06:
				return CalibrateCode;
			case 0x07:
				return CalculateCode;
			case 0x08:
				return ModuleEnableCode;
			case 0x09:
				return FlashParamsCode;
			case 0x0A:
				return UpgradeCode;
			case 0x0B:
				return ConnectCode;
			case 0x0C:
				return VoltBoundaryCode;
			case 0x0D:
				return InfoCode;
			case 0x0E:
				return HardErrInfoCode;
			case 0x10:
				return PulseCode;
			case 0x11:
				return InfoExCode;
			case 0x12:
				return PickupExCode;
			case 0x13:
				return CalibrateExCode;
			case 0x14:
				return CalculateExCode;
			case 0x15:
				return FlashParamsExCode;
			case 0x16:
				return IPAddressCode;
			case 0x19:
				return VoltBoundaryExCode;
			case 0x20:
				return SoftInfoCode;
			case 0x21:
				return MultiModuleFlashParamsCode;
			case 0x22:
				return MultiModuleCalibrateCode;
			case 0x23:
				return MultiModuleCalculateCode;
			case 0x24:
				return DriverSelfCheckCode;

			}

			return null;
		}

	}

	/**
	 * 采集板工作环境定义
	 * 
	 * @author Administrator
	 *
	 */
	public enum WorkEnv {

		WORK, CAL, UPGRADE, MODULE_UPGRADE, AD_UPGRADE, CHECK_UPGRADE;
	}

	public enum CalWorkMode {
		SLEEP, CC, CV, DC, DV
	}

	public enum CalBackupWorkMode {

		SLEEP, CV;
	}

	public enum Pole {

		POSITIVE, NEGTIVE;
	}

	/**
	 * 通道状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum WorkState {

		NONE, UDT, RUN, STOP, EXCEPT, COMPLETE;
	}

	/**
	 * 故障类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum AlertType {

		NORMAL, VOLT_UPPER, CURR_UPPER, POLE_REVERSE, ERROR, OVER_TEMP, MOUDLE_COMM, MAIN_COMM, MODULE_FIRM, OTHER
	}

	public enum AlertTypeEx {

		NORMAL(0), VOLT_UPPER(1), CURR_UPPER(2), POLE_REVERSE(3), ERROR(4), OVER_TEMP(5), MOUDLE_COMM(11), MAIN_COMM(
				12), MODULE_FIRM(13), BACK_VOLT_UPPER(14), POWER_VOLT_UPPER(15), AD_COMM(16), OTHER(17), CHECK_COMM(18);

		private int code;

		private AlertTypeEx(int code) {

			this.code = code;
		}

		public static AlertTypeEx parseCode(int code) {

			switch (code) {

			case 0:
				return NORMAL;
			case 1:
				return VOLT_UPPER;
			case 2:
				return CURR_UPPER;
			case 3:
				return POLE_REVERSE;
			case 4:
				return ERROR;
			case 5:
				return OVER_TEMP;
			case 11:
				return MOUDLE_COMM;
			case 12:
				return MAIN_COMM;
			case 13:
				return MODULE_FIRM;
			case 14:
				return BACK_VOLT_UPPER;
			case 15:
				return POWER_VOLT_UPPER;
			case 16:
				return AD_COMM;
			case 17:
				return OTHER;
			case 18:
				return CHECK_COMM;

			}

			return null;
		}

	}

	/**
	 * 开关状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum SwitchState {

		CLOSE, OPEN
	}

	public enum Opt {

		STOP, STARTUP
	}

	public enum ReadyFlag {

		READY, UNREADY, ERROR;
	}

	public enum ConnectState {

		DISCONNECT, CONNECT;
	}

	public static class CalDot {

		public double meter; // 表值
		public double adc;
		public double adcK;
		public double adcB;
		public long da;
		public double programK;
		public double programB;

	}

	public enum ADCheck {

		OK, ERR;

	}

	public enum CalibrateCheck {

		OK, ERR;
	}

	public enum PulseMode {

		CC, DC, REST;

	}

	/**
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public enum CheckResult {

		NORMAL, MISS, CAL_ERR, IN_BOOT;

	}

	public enum FanType {

		INVERT, AUXILIARY
	}

	public static class Fan {

		public FanType fanType;

		public boolean stateOk;

	}

	public enum CalMode {

		SLEEP(0), CC(1), CV(2), DC(5);

		private int code;

		private CalMode(int code) {

			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static CalMode valueOf(int code) {

			switch (code) {
			case 0:
				return SLEEP;
			case 1:
				return CC;
			case 2:
				return CV;
			case 5:
				return DC;

			}

			return null;
		}

	}

}
