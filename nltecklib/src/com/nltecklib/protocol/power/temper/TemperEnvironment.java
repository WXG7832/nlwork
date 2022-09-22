package com.nltecklib.protocol.power.temper;

import com.nltecklib.protocol.power.Environment.Code;

/**
 */
public class TemperEnvironment {

	public enum TemperCode implements Code {
		/**
		 * 功能码常量定义
		 */
		TempSwPickCode(0x01), OutControlCode(0x02), UpgradeCode(0x03), VersionCode(0x04), WorkPattern(0x05),AddressCode(0x06),
		TemperAdjustCode(0x07);

		private int code;

		private TemperCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {

			return code;
		}

		public static TemperCode valueOf(int code) {

			switch (code) {

			case 0x01:
				return TempSwPickCode;
			case 0x02:
				return OutControlCode;
			case 0x03:
				return UpgradeCode;
			case 0x04:
				return VersionCode;
			case 0x05:
				return WorkPattern;
			case 0x06:
				return AddressCode;
			case 0x07:
				return TemperAdjustCode;

			}

			return null;

		}
		
		public enum WorkPattern {

			WORK, UPGRADE;
		}

	}
}
