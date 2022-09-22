package com.nltecklib.protocol.fuel.alert;

import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 报警板
 * 
 * @author Administrator
 *
 */
@Deprecated
public class AlertEnviroment {
    /**
     * 报警板功能码
     * 
     * @author Administrator
     *
     */
    public enum AlertCode implements Code {
	/**
	 * 常量定义
	 */
	PICK_UP_CODE(1), COLOR_LIGHT_CODE(2), BUZZER_CODE(3), WORK_MODE_CODE(4), CAL_PICKUP_CODE(5);

	private int code;

	private AlertCode(int funCode) {

	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static AlertCode getCode(int code) {
	    for (AlertCode col : AlertCode.values()) {
		if (col.getCode() == code) {
		    return col;
		}
	    }
	    return null;
	}

    }

    /**
     * 三色灯
     * 
     * @ClassName LightType
     * @Description
     * @author zhangly
     * @Date 2019年9月19日 上午11:08:17
     */
    public enum LightType implements Code {
	/**
	 * 常量定义
	 */
	NONE(0), RED(1), YELLOW(2), RED_YELLOW(3), GREEN(4), RED_GREEN(5), YELLOW_GREEN(6), ALL(7);

	private int code;

	private LightType(int funCode) {
	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static LightType getCode(int code) {
	    for (LightType l : LightType.values()) {
		if (l.getCode() == code) {
		    return l;
		}
	    }
	    return null;
	}

    }

    public enum H2State {
	OFF, LOW, HIGH;
    }

}
