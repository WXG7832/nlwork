package com.nltecklib.protocol.li.test.driver;

import com.nltecklib.protocol.li.Environment.Code;


/**
* @author  wavy_zheng
* @version 创建时间：2020年11月24日 上午10:29:43
* 驱动板测试协议
*/
public class DriverTestEnvironment {
    
	
	public enum DriverTestCode implements Code {
		
		
		PowerSwitch(0X01),PowerState(0x02),PowerConfig(0x03),RelayCode(0x04);

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}
		
		private int code;
		
		private DriverTestCode(int code) {
			
			this.code = code;
		}
		
		public static DriverTestCode valueOf(int code) {
			
			for (DriverTestCode item : DriverTestCode.values()) {
				if (item.getCode() == code) {
					return item;
				}
			}
			return null;
		}
		
		
		
	}
	
	public enum PowerType {
		
		PROVIDE/*供电电源*/ , INVERTER/*逆变电源*/
		
	}
	
	/**
	 * 烧录继电器切换类型
	 * @author wavy_zheng
	 * 2020年11月25日
	 *
	 */
	public enum BurnRelayType {
		
		LOGIC , CHECK
	}
	
	
	
	
}
