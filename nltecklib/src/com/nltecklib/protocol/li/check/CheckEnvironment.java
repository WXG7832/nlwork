package com.nltecklib.protocol.li.check;

import com.nltecklib.protocol.li.Environment.Code;

public class CheckEnvironment {

	public enum CheckCode implements Code {

		PoleCode(0x02), BaseCountCode(0x03), PickupCode(0x07), MultiPickupCode(0x08), StartupCode(
				0x0b), DeviceProtectCode(0x12), WriteCalFlashCode(0x14), ChnCalCode(0x15), FaultCheckCode(0x16),
		CalculateCode(0x19),
		/**
		 * HK
		 */
		HKCalibrateCode(0x17),
		HKDriverState(0x18);
		
		private int code;

		private CheckCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static CheckCode valueOf(int code) {

			switch (code) {

			case 2:
				return PoleCode;
			case 3:
				return BaseCountCode;
			case 7:
				return PickupCode;
			case 8:
				return MultiPickupCode;
			case 0x0b:
				return StartupCode;
			case 0x12:
				return DeviceProtectCode;
			case 0x14:
				return WriteCalFlashCode;
			case 0x15:
				return ChnCalCode;
			case 0x16:
				return FaultCheckCode;		
			case 0x17:
				return HKCalibrateCode;	
			case 0x18:
				return HKDriverState;
			case 0x19:
				return CalculateCode;
			}
			return null;
		}

	}
	
	public enum StartupState {

		CHECK(0x00), CAL(0x05);

		private int code;

		private StartupState(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}

		public static StartupState valuesOf(int code) {

			switch (code) {

			case 0:
				return CHECK;
			case 5:
				return CAL;
			}

			return null;
		}
	}
	
	public enum PowerState {

		POWEROFF, POWERON;
	}

	public enum HKAlertCode {

		NORMAL, DEV_VOLT_OVER, HARD_ERROR;
	}
	
	public enum AlertCode {

		NORMAL, DEV_VOLT_OVER, POLE_REVERSE, HARD_ERROR;
	}

	public enum ChnState {

		NONE, NORMAL, EXCEPT;
	}

//	public enum Pole {
//
//		NORMAL, REVERSE
//	}

	public enum WorkMode {

		CHECK, CAL, HARDERR;
	}
	
	public enum Work {

		NONE, CONSTANT;
	}

	/**
	 * 校准点格式
	 * 
	 * @author Administrator
	 *
	 */
	public static class CalDot {

		public double adc;
		public double adcK;
		public double adcB;

	}

	public enum SwitchState {

		OFF, ON;
	}

}
