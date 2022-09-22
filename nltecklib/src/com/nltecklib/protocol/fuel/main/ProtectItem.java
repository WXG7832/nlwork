package com.nltecklib.protocol.fuel.main;

/**
 * 器件上下限保护
 * 
 * @author guofang_ma
 *
 */
public class ProtectItem {

    public double standard;
    public double alertVal;
    public double stopVal;

    public ProtectItem(double standard, double alertVal, double stopVal) {
	this.standard = standard;
	this.alertVal = alertVal;
	this.stopVal = stopVal;
    }

    public ProtectItem() {

    }

    public double getStandard() {
	return standard;
    }

    public void setStandard(double standard) {
	this.standard = standard;
    }

    public double getAlertVal() {
	return alertVal;
    }

    public void setAlertVal(double alertVal) {
	this.alertVal = alertVal;
    }

    public double getStopVal() {
	return stopVal;
    }

    public void setStopVal(double stopVal) {
	this.stopVal = stopVal;
    }

    @Override
    public String toString() {
	return "ProtectItem [standard=" + standard + ", alertVal=" + alertVal + ", stopVal=" + stopVal + "]";
    }

}
