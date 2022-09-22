package com.nltecklib.protocol.plc.pief44.model;

public class Alert {
	private int value;
	private int max;
	private int min;
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	@Override
	public String toString() {
		return "Alert [value=" + value + ", max=" + max + ", min=" + min + "]";
	}
	public Alert(int value, int max, int min) {
		super();
		this.value = value;
		this.max = max;
		this.min = min;
	}
	public Alert() {
		super();
	}
	
}
