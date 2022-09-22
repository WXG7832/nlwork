package com.nltecklib.protocol.power.driver;

import com.nltecklib.protocol.power.Environment.Code;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月15日 下午2:08:51 驱动板协议环境配置
 */
public class DriverEnvironment {
    
	
	
	public enum DriverCode implements Code {
		/**
		 * 功能码常量定义
		 */
		PoleCode(0x01), InfoCode(0x02), ModeCode(0x03), HeartbeatCode(0x04), StepCode(0x05), OperateCode(
				0x06), PickupCode(0x07), CheckCode(0x08), ProtectCode(0x09), ResumeCode(0x0A), UpgradeCode(0x0B),
		ChannelTemperCode(0x36), ChannelVoltCode(0x37),
		/**
		 * 校准功能码
		 */
		FlashParamCode(0x31), CalibrateCode(0x32), CalculateCode(0x33), ModuleSwitchCode(0x34), MatchAdcCode(0x35) ,
		
		/**
		 * 特殊协议
		 */
		DriverAddressCode(0xF0)
		;

		private int code;

		private DriverCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {

			return code;
		}

		public static DriverCode valueOf(int code) {

			switch (code) {

			case 0x01:

				return PoleCode;
			case 0x02:
				return InfoCode;
			case 0x03:
				return ModeCode;
			case 0x04:
				return HeartbeatCode;
			case 0x05:
				return StepCode;
			case 0x06:
				return OperateCode;
			case 0x07:
				return PickupCode;
			case 0x08:
				return CheckCode;
			case 0x09:
				return ProtectCode;
			case 0x0A:
				return ResumeCode;
			case 0x0B:
				return UpgradeCode;

			case 0x31:
				return FlashParamCode;
			case 0x32:
				return CalibrateCode;
			case 0x33:
				return CalculateCode;
			case 0x34:
				return ModuleSwitchCode;
			case 0xF0:
				return DriverAddressCode;
			case 0x36:
				return ChannelTemperCode;
			case 0x37:
				return ChannelVoltCode;

			}

			return null;

		}

	}

	public enum Pole {

		NEGTIVE, POSITIVE;
	}

	/**
	 * 驱动板工作模式
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public enum DriverMode {

		NORMAL, WORK, CAL, JOINT, DRIVER_UPGRADE, CHECK_UPGRADE, PICK_UPGRADE, TEMP_UPGRADE;

	}

	/**
	 * 驱动板流程步次工作模式
	 * 
	 * @author wavy_zheng 2020年11月16日
	 *
	 */
	public enum WorkMode {

		SLEEP, CC, CV, DC, CC_CV , DW;
	}

	/**
	 * 通道报警码
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public enum AlertCode {

		NORMAL, OVER_VOLT, OVER_CURR, POLE_REVERSE, HARDERR, OVER_TIME, DEVICE_VOLT_UPPER;

	}

	/**
	 * 校准模式
	 * 
	 * @author wavy_zheng 2021年12月16日
	 *
	 */
	public enum CalMode {

		SLEEP, CC, CV, DC;

	}
	
	/**
	 * 实验室校准模式
	 * 
	 */
	public enum LabCalMode {

		SLEEP, CC, CV, CW, CR, DC;

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

	/**
	 * 通道状态
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public enum ChnState {

		NONE(0), UDT(1), RUNNING(2), STOP(4), CLOSE(6), EXCEPT(7), COMPLETE(8);

		private int code;

		private ChnState(int code) {

			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static ChnState valueOf(int code) {

			switch (code) {
			case 0:
				return NONE;
			case 1:
				return UDT;
			case 2:
				return RUNNING;
			case 4:
				return STOP;
			case 6:
				return CLOSE;
			case 7:
				return EXCEPT;
			case 8:
				return COMPLETE;

			}

			return null;

		}

	}

	
	
	

}
