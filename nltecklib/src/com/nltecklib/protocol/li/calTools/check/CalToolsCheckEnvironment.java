package com.nltecklib.protocol.li.calTools.check;

import com.nltecklib.protocol.li.Environment.Code;

public class CalToolsCheckEnvironment {

	
	public enum CalToolsCheckCode implements Code {
		Address(0x70),CalFactorCode(0x71),MeterCode(0x72),CalculateCode(0x73),SerialTestCode(0x75),TestModeCode(0x76),PowerRelayCode(0x77);

		private int code;
		
		private CalToolsCheckCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}
		
		public static CalToolsCheckCode valueOf(int code) {
			for (CalToolsCheckCode item : CalToolsCheckCode.values()) {
				if(item.getCode()==code) {
					return item;
				}
			}
			return null;
		}
	}
	
}
