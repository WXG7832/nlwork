package com.nltecklib.protocol.lab.cal;

import com.nltecklib.protocol.lab.Environment.Code;

/**
 * PC与设备主控 关于校准的通信协议
 * 
 * @author Administrator
 *
 */
public class CalEnvironment {

	public enum WorkState {

		UNWORK, WORK
	}

	public enum ReadyState {

		UNREADY, OTHER_READY, TEMP_READY
	}

	public enum ErrCode {

		NORMAL, OVER_CHARGE, OVER_DISCHARGE, OVER_VOLT, REVERSE_VOLT, OVER_TEMP
	}

	public enum BaseVolt {

		NONE, HALF, ONE_HALF, THREE, FOUR_EIGHT
	}

	public enum CalCode implements Code {

		CAL(0x01), VOLT_BASE(0x02), TEMP(0x04), RESISTER(0x05), HEARTBEAT(0x06), CALCULATE(0x07), IP_ADDRESS(0x08),
		CAL2(0x09) , SwitchMeter(0x0A), ADDRESS(0x0B),  RESISTER2(0x0D), CAL3(0x0E), CALCULATE3(0x0C);

		@Override
		public int getCode() {

			return code;
		}

		private int code;

		private CalCode(int funCode) {

			this.code = funCode;
		}

		public static CalCode valueOf(int code) {

			for (CalCode temp : CalCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return null;
		}

	}

}
