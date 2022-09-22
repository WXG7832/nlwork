package com.nltecklib.protocol.li.driver;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * Çý¶¯°å»·¾³
 * 
 * @author Administrator
 *
 */
public class DriverEnvironment {

    public enum DriverCode implements Code {

	PoleCode(0x02), ChnSwitchCode(0x03), ChnPickupDataCode(0x04), ProcedureCode(0x05), WorkModeCode(0x07), CalculateCode(0x08),
	MeterCode(0x09), DieSwitchCode(0x0A), CalibrationFactorCode(0x0B), SelfCheckCode(0x0C), CheckCalFactorCode(0x0D),
	MultiModePickupCode(0x0E), TransferProcessCode(0x0F), VERSION_CODE(0x11),DRIVER_CHIPID(0x12), START_INIT_CODE(0x13), CALIBRATE_UUID_CODE(0x15),
	ProgramStateCode(0x16),UPGRADE_CODE(0x17), AnalogChnSwitchCode(0x20), MultiTransferProcessCode(0x21), SelfCheck2Code(0x22),
	Driver200aDiapCalCode(0x24), Driver200aCalibrationFactorCode(0x25), Driver200aMeterCode(0x26), Driver200aMultiDiapTestCode(0x27),
	Driver200aDieSwitchCode(0x28), Driver200aAnalogChnSwitchCode(0x29), Driver200aProcedureCode(0x2A),MultiTransferProcessCode200a(0x2B);

	private int code;

	private DriverCode(int funCode) {

	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static DriverCode valueOf(int code) {

	    for (DriverCode driver : DriverCode.values()) {
		if (driver.getCode() == code) {
		    return driver;
		}
	    }

	    return null;
	}

    }

    public enum WorkMode {

	SLEEP, CC, CV, DC, CCCV
    }

    public static class CalDot {

	public double adc;
	public double adcK;
	public double adcB;
    }

    public enum UuidType {

	LOGIC_DRIVER, CHECK_DRIVER;
    }

}
