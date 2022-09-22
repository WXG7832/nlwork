package com.nltecklib.protocol.li.calTools.test;

import com.nltecklib.protocol.li.Environment.Code;

public class CalToolsTestEnvironment {

	public enum CalToolsTestCode implements Code {
		PowerCode(0x00), LoadSwitchCode(0x01), SerialSelectCode(0x02), ChnSelectCode(0x03), DacCode(0x04),
		VoltagePolarityCode(0x05), CurrentPickupCode(0x06), CurrentProtectionCode(0x07),CalculateCode(0x08);

		private int code;

		private CalToolsTestCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static CalToolsTestCode valueOf(int code) {
			for (CalToolsTestCode item : CalToolsTestCode.values()) {
				if (item.getCode() == code) {
					return item;
				}
			}
			return null;
		}
	}
	
	public enum TestPole{
		Positive(0,"正电压"),Negative(1,"负电压");
		
		private int code;
		private String description;
		private TestPole(int code ,String description) {
			this.code=code;
			this.description=description;
		}
		public int getCode() {
			return code;
		}
		public String getDescription() {
			return description;
		}
		
		public static TestPole valueOf(int code) {
			for (TestPole temp : TestPole.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return null;
		}
	}

}
