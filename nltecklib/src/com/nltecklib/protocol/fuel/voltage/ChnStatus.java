package com.nltecklib.protocol.fuel.voltage;

import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.ChnVolStatus;

public class ChnStatus {
    private ChnVolStatus chnVolStatus;
    private double chnExceptionVol;

    public ChnVolStatus getChnVolStatus() {
	return chnVolStatus;
    }

    public void setChnVolStatus(ChnVolStatus chnVolStatus) {
	this.chnVolStatus = chnVolStatus;
    }

    public double getChnExceptionVol() {
	return chnExceptionVol;
    }

    public void setChnExceptionVol(double chnExceptionVol) {
	this.chnExceptionVol = chnExceptionVol;
    }

    @Override
    public String toString() {
	return "ChnStatus [chnVolStatus=" + chnVolStatus + ", chnExceptionVol=" + chnExceptionVol + "]";
    }

}
