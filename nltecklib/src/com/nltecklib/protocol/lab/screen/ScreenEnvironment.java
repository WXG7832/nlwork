package com.nltecklib.protocol.lab.screen;

import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 上午10:48:41
* 类说明
*/
public class ScreenEnvironment {
   
	
	public enum ScreenCode implements Code {
		
		TitleCode(0x01),UnitCode(0x02),ChnCountCode(0x03),ChnStateCode(0x04),
		CommunicateCode(0x05),IpAddressCode(0x06),AddressCode(0x0A),LEDCode(0x0B);
        
		private int code;

		private ScreenCode(int funCode) {

			this.code = funCode;
		}
		
		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}
		
		public static ScreenCode valueOf(int code) {
			
			for (ScreenCode temp : ScreenCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return null;
			
		}
		
		
		
	}
	
	public enum ChnState {
		
		NONE , REVERSE , COMPLETE , UDT , ALERT
	}
	
	public enum Title {
		
		V5A6(0x01)/*5v6A测试柜*/;
		
		private int code;

		
		private Title(int code) {
			
			this.code = code;
		}
		
		
		public int getCode() {
			
			return this.code;
		}
	}
	
	
	public enum Led {
		
		OFF , GREEN , YELLOW , RED;
		
	}
	
	
	
}
