package com.nltecklib.protocol.fuel.protect;

import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 保护板环境
 * 
 * @author Administrator
 *
 */
public class ProtectEnviroment {
	/**
	 * 保护板功能码
	 * 
	 * @author Administrator
	 *
	 */
	public enum ProtectCode implements Code {
		PICK_UP_CODE(1), SOV_CODE(2), POWER_CODE(3), STATE_READ_CODE(4), H2_CONCENTRATION_CODE(5), RGB_LIGHT(6),
		ACTION_DELAY(7), UPDATE_MODE_CODE(0x08), UPDATE_CODE(0x09), N2_TIME_DURATION_CODE(0x0A), PUMP_CODE(12),
		WORK_MODE_CODE(13), CAL_PICKUP_CODE(14);

		private int code;

		private ProtectCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static ProtectCode getCode(int code) {
			for (ProtectCode col : ProtectCode.values()) {
				if (col.getCode() == code) {
					return col;
				}
			}
			return null;
		}

	}

}
