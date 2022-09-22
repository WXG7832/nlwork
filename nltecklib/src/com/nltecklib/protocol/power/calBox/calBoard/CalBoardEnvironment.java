package com.nltecklib.protocol.power.calBox.calBoard;

import com.nltecklib.protocol.power.Environment.Code;

/**
 * 校准环境量
 * 
 * @author Administrator
 *
 */
public class CalBoardEnvironment {

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

    public enum CalBoardCode implements Code {

	CAL(0x01), MATCH_DOT(0x02), CONNECT_METER(0x03), CALBOARD_INDEX(0x06), NEW_CAL(0x07), 
	EXRESISTER(0x08), CALIBRATE_DOT(0x09), MEASURE_DOT(0X0A), UPDATE_SWITCH(0x0B), UPDATE(0x0C), 
	TEMP_SWITCH(0x0D), TEMP(0x0E), CONDUCTANCE(0x10);

	@Override
	public int getCode() {

	    return code;
	}

	private int code;

	private CalBoardCode(int funCode) {

	    this.code = funCode;
	}

	public static CalBoardCode valueOf(int code) {

	    for (CalBoardCode temp : CalBoardCode.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }

	    return null;
	}

    }

}
