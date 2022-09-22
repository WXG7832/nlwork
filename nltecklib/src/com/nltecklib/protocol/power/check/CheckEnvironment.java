package com.nltecklib.protocol.power.check;

import com.nltecklib.protocol.power.Environment.Code;

/**
 */
public class CheckEnvironment {

	public enum CheckCode implements Code {
		/**
		 * 功能码常量定义
		 */
		WorkEnvCode(0x01), ChnnPickCode(0x02), SwitchCode(0x03), VolPickCode(0x04), ChnnCalCode(0x05), ChnnVarCode(
				0x06), FlashCode(0x07), BatteryProtecCode(0x08), UpgradeCode(0x09), VersionCode(0x0A), DeviceStateCode(0x0B);

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

			case 0x01:
				return WorkEnvCode;
			case 0x02:
				return ChnnPickCode;
			case 0x03:
				return SwitchCode;
			case 0x04:
				return VolPickCode;
			case 0x05:
				return ChnnCalCode;
			case 0x06:
				return ChnnVarCode;
			case 0x07:
				return FlashCode;
			case 0x08:
				return BatteryProtecCode;
			case 0x09:
				return UpgradeCode;
			case 0x0A:
				return VersionCode;
			case 0x0B:
				return DeviceStateCode;

			}

			return null;

		}

	}

	public enum WorkPattern {

		WORK, CAL, UPGRADE;
	}

	public enum PickupState {

		START, STOP;
	}

	public enum SwitchState {

		CLOSE, OPEN;
	}

	public enum Alarm {

		NONE, EXIST;
	}
	
	public enum DeviceState {

		NORMAL, MISSING, EXCEP, BOOT;
	}
}
