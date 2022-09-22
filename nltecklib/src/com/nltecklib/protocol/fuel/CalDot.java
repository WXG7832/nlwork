package com.nltecklib.protocol.fuel;

public class CalDot{
	private double adc;
	private double adck;
	private double adcb;
	public double getAdc() {
		return adc;
	}
	public void setAdc(double adc) {
		this.adc = adc;
	}
	public double getAdck() {
		return adck;
	}
	public void setAdck(double adck) {
		this.adck = adck;
	}
	public double getAdcb() {
		return adcb;
	}
	public void setAdcb(double adcb) {
		this.adcb = adcb;
	}
	@Override
	public String toString() {
		return "CalDot [adc=" + adc + ", adck=" + adck + ", adcb=" + adcb + "]";
	}
	

}
