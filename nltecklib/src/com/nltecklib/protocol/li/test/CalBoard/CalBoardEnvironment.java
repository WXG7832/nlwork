/**
 * 
 */
package com.nltecklib.protocol.li.test.CalBoard;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * 
 * @Description: 校准基板测试协议
 * @version: v1.0.0
 * @author Admin
 *
 */
public class CalBoardEnvironment {

	public enum CalBoardTestCode implements Code {

		WorkPattern(0X01), AddressConfig(0x02),PowerSwitch(0x03);

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}

		private int code;

		private CalBoardTestCode(int code) {

			this.code = code;
		}

		public static CalBoardTestCode valueOf(int code) {

			for (CalBoardTestCode item : CalBoardTestCode.values()) {
				if (item.getCode() == code) {
					return item;
				}
			}
			return null;
		}

	}

	
	public enum BoardType {

		/**模拟电源*/AnalogPower, /**电子负载*/Electronic
	}

	public enum WorkPattern {

		Sleep, /*Cc,Cv,Dc*/ /**充电*/ Charge, /**放电*/ DisCharge
	}

	public enum PrecisionSwitch {
		LowPreci, HighPreci,HigherPreci
	}

	public enum PowerSwitch {
		Close, Open
	}


}
