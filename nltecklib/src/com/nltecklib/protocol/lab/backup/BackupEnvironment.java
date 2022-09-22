package com.nltecklib.protocol.lab.backup;

import com.nltecklib.protocol.lab.Environment.Code;

public class BackupEnvironment {

    public enum BackupCode implements Code {

	WorkStateCode(0x01), PickupCode(0x02), DefineVoltCode(0x03), ChnWorkModeCode(0x04), BatterProtectCode(0x05), CalChnCode(0x06), CheckChnCode(0x07), 
	FlashCode(0x08), UpdateCode(0x09), DcRelayCode(0x0A), CurrDelayCode(0x0B), VersionInfoCode(0x0C), HardErrCode(0x0E) , LedCode(0x0F);

	private int code;

	private BackupCode(int code) {
	    this.code = code;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static BackupCode valueOf(int code) {
	    for (BackupCode temp : BackupCode.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }
	    return null;
	}
    }

    /**
     * ×´Ì¬±êÊ¶
     * 
     * @author Administrator
     *
     */
    public enum StateIdent {

	WORK, UPGRADE;

    }

    public enum RunEnv {
	NONE, WORK
    }

    public enum ChnState {
	NO_BATTERY, NORMAL, CAL, ERROR
    }

    public enum AlertCode {
	NORMAL, BACKUP_VOLY_OVER, POWER_VOLY_OVER, BATTER_REVERSE,
	FAN_ERR1, FAN_ERR2 , FAN_ERR3 , FAN_ERR4;
    }

    public enum SetChnState {
	WORK, CAL
    }

    public enum ConnectState {

	DISCONNECT, CONNECT;
    }

    public enum ADCheck {

	OK, ERR;

    }
    
    public enum LedState {
    	
    	
    	OFF , GREEN , RED;
    }

    public enum CalibrateCheck {

	OK, ERR;
    }
}
