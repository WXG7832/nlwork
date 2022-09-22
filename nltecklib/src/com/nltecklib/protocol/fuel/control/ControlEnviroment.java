package com.nltecklib.protocol.fuel.control;

import com.nltecklib.protocol.fuel.Environment.Code;

public class ControlEnviroment {
    /**
     * ¿ØÖÆ°å¹¦ÄÜÂë
     * 
     * @author caichao_tang
     *
     */
    public enum ControlCode implements Code {

	PICK_UP_CODE(1), WT_CODE(2), SOV_CODE(3), FLOW_CODE(4), PUMP_CODE(5), ASSOCIATION_CODE(6), PID_CODE(7), WORK_MODE_CODE(8), CAL_PICKUP_CODE(9), TEMP_CODE(0x0a), EF_FLOW_CODE(0x0c), SOLID_RELAY_CODE(0x0d), EF_CAPACITY_UPPER_LIMIT_CODE(0x0e), PRESSURE_UP_LIMIT(0x0f), TEMP_UP_LIMIT(0x10), STACK_PRESSURE_DIFFERENCE(0x11), UPDATE_CODE(0x12), ALERT_CLEAR_CODE(0x13), N2_CODE(106), N2_STATE_CODE(107);

	private int code;

	private ControlCode(int funCode) {

	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static ControlCode getCode(int code) {
	    for (ControlCode col : ControlCode.values()) {
		if (col.getCode() == code) {
		    return col;
		}
	    }
	    return null;
	}

    }

}
