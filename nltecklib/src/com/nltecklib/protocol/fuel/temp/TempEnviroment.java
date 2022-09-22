package com.nltecklib.protocol.fuel.temp;

import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 温控板环境
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TempEnviroment {
    /**
     * 温控板功能码
     * 
     * @author caichao_tang
     *
     */
    public enum TempCode implements Code {
	/**
	 * 常量定义
	 */
	PICK_UP_CODE(1), TEMP_ALERT_CODE(2), TEMP_CODE(3), SOV_CODE(4), FLOW_CODE(5), VARIABLE_PUMP_CODE(6), VARIABLE_PUMP_SWITCH_CODE(7), WORK_MODE_CODE(8), PID_CODE(9), CAL_PICKUP_CODE(0x0b);

	private int code;

	private TempCode(int funCode) {

	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static TempCode getCode(int code) {
	    for (TempCode temp : TempCode.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }
	    return null;
	}

    }

}
