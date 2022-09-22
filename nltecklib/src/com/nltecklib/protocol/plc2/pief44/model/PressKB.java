package com.nltecklib.protocol.plc2.pief44.model;

public class PressKB {
	private int K;
	private int B;
	public int getK() {
		return K;
	}
	public void setK(int k) {
		K = k;
	}
	public int getB() {
		return B;
	}
	public void setB(int b) {
		B = b;
	}
	@Override
	public String toString() {
		return "PressKB [K=" + K + ", B=" + B + "]";
	}
	public PressKB(int k, int b) {
		super();
		K = k;
		B = b;
	}
	public PressKB() {
		super();
	}
	
}
