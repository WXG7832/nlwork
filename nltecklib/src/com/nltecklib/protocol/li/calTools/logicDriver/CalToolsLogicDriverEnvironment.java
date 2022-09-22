package com.nltecklib.protocol.li.calTools.logicDriver;

import com.nltecklib.protocol.li.Environment.Code;

public class CalToolsLogicDriverEnvironment {

	
	public enum CalToolsLogicDriverCode implements Code {
		Address(0x70),SerialTestCode(0x75);

		private int code;
		
		private CalToolsLogicDriverCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}
		
		public static CalToolsLogicDriverCode valueOf(int code) {
			for (CalToolsLogicDriverCode item : CalToolsLogicDriverCode.values()) {
				if(item.getCode()==code) {
					return item;
				}
			}
			return null;
		}
	}
}
