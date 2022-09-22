package com.nltecklib.protocol.li.cal;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * 校准环境量
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

	public enum Pole {

		REVERSE, NORMAL
	}

	public enum WorkMode {

		SLEEP, CC, CV, DC, DV
	}

	public enum BaseVolt {

		NONE, HALF, ONE_HALF, THREE, FOUR_EIGHT
	}

	public enum CalCode implements Code {

		CAL(0x01), VOLT_BASE(0x02), RELAY(0x03), TEMP(0x04), RESISTER(0X05), ADDRESS_CAL(0x06), NEW_CAL(0x07),
		EXRESISTER(0x08), CAL2(0x09) , MEASURE2(0X0a), UpdateModeCode(0x0B), UpdateFileCode(0x0C), TempControlCode(0x0D),
		OverTempAlertCode(0X0E), WorkModeCode(0X0F), ResistanceCode(0x10), TestPatternCode(0x11), ELoadOverTempAlarmCode(0x12),
		RelaySwitch(0x13), RelayEx(0x14),ResistanceRelayCode(0x15) ;
		
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
	
	public enum OverTempAlert{
		NONE,UPPER,LOWER
	}
	
	public enum ElecTempAlert{
		NONE,ALERT
	}
	
	public enum ConstantTempAlert{
		NONE,ALERT
	}
	
	public enum DeviationAlert{
		NONE,ALERT
	}
	
	public enum WorkPattern {

		SLEEP, CC, CV, DC
	}
	
	/**
	 * 测试类型
	 */
	public enum TestType {

		NONE, VOL_COMPARE, POSITIVE_SHORT_CIRCUIT, NEGATIVE_SHORT_CIRCUIT,OTHER
	}
	
}
