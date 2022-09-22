package com.nltecklib.protocol.lab.adCal;

import com.nltecklib.protocol.lab.Environment.Code;

/**
 * PC与AD采集板_校准板(M20)的通信协议
 * 
 * @author zemin_zhu
 *
 */
public class CalEnvironment {
    public final static int ADC_RESO = 100000; // 小数点后长度
    public final static int KB_EXP = 8; // 计算kb放大倍数的指数

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

    public enum ADCalCode implements Code {

	CHN_STATE(0x01), GET_VOLT(0x02), CALDOT_P140MV(0x03), CALDOT_N140MV(0x04), CALDOT_5MV(0x05), METERDOT_AD1(0x06), METERDOT_AD0(0x07), FLASHDATA_P140MV(0x08), FLASHDATA_N140MV(0x09), FLASHDATA_P5MV(0x0A), FLASHDATA_N5MV(0x0B), UPDATE(0x0C), VERSION(0x0D), RESET(0xE);

	@Override
	public int getCode() {

	    return code;
	}

	private int code;

	private ADCalCode(int funCode) {

	    this.code = funCode;
	}

	public static ADCalCode valueOf(int code) {

	    for (ADCalCode temp : ADCalCode.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }
	    return null;
	}

    }

    // 一组ADC与它的kb
    public static class ADC_KB {
	public byte chnIdx = 0;
	public double adc = 0.0;
	public double k = 0.0;
	public double b = 0.0;
	public double adc_orig = 0.0;

    }

}
