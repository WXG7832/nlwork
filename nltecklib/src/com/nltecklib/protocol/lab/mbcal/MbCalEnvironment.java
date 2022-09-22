package com.nltecklib.protocol.lab.mbcal;

import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午8:28:03
*  设备主控与校准板的通信协议
*/
public class MbCalEnvironment {
       
	public enum MbCalCode implements Code {

		CAL(0x01), VOLT_BASE(0x02), TEMP(0x04), RESISTER(0x05), HEARTBEAT(0x06), CALCULATE(0x07), IP_ADDRESS(0x08),
		CAL2(0x09) , DATE(0x0A) , SWITCH_METER(0X0B);

		@Override
		public int getCode() {

			return code;
		}

		private int code;

		private MbCalCode(int funCode) {

			this.code = funCode;
		}

		public static MbCalCode valueOf(int code) {

			for (MbCalCode temp : MbCalCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return null;
		}

	}
}
