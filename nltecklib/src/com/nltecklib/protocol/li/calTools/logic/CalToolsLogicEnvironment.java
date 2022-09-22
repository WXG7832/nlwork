package com.nltecklib.protocol.li.calTools.logic;

import com.nltecklib.protocol.li.Environment.Code;

public class CalToolsLogicEnvironment {

	
	public enum CalToolsLogicCode implements Code {
		Address(0x70),CalFactorCode(0x71),MeterCode(0x72),CalculateCode(0x73),SerialTestCode(0x75),TestModeCode(0x76),SelfTestCode(0x78);

		private int code;
		
		private CalToolsLogicCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}
		
		public static CalToolsLogicCode valueOf(int code) {
			for (CalToolsLogicCode item : CalToolsLogicCode.values()) {
				if(item.getCode()==code) {
					return item;
				}
			}
			return null;
		}
	}
	

}
