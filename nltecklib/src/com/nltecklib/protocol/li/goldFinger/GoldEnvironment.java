package com.nltecklib.protocol.li.goldFinger;


import com.nltecklib.protocol.li.Environment.Code;

/**
 * Ω ÷÷∏
 * 
 * @author Administrator
 *
 */
public class GoldEnvironment {

	public enum GoldCode implements Code {

		GoldFinCode(0x01);

		private int code;

		private GoldCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static GoldCode valueOf(int code) {

			switch (code) {

			case 1:
				return GoldFinCode;
			
			}

			return null;
		}

	}

}
