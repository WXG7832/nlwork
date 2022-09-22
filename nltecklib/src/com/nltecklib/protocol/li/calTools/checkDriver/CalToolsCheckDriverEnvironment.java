package com.nltecklib.protocol.li.calTools.checkDriver;

import com.nltecklib.protocol.li.Environment.Code;

public class CalToolsCheckDriverEnvironment {

	
	public enum CalToolsCheckDriverCode implements Code {
		Address(0x70),SerialTestCode(0x75);

		private int code;
		
		private CalToolsCheckDriverCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}
		
		public static CalToolsCheckDriverCode valueOf(int code) {
			for (CalToolsCheckDriverCode item : CalToolsCheckDriverCode.values()) {
				if(item.getCode()==code) {
					return item;
				}
			}
			return null;
		}
	}

}
