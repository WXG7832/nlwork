package com.nltecklib.protocol.plc.pief44.model;

public class Speed {
	private float prePress;
	private int jogSpeed;
	private int firstSpeed;
	private int secondSpeed;
	private int thirdSpeed;
	private int fourthSpeed;
	private int fifthSpeed;
	private int returnSpeed;
	private int compansateSpeed;
	private int pressChangeSpeed;
	public float getPrePress() {
		return prePress;
	}
	public void setPrePress(float prePress) {
		this.prePress = prePress;
	}
	public int getJogSpeed() {
		return jogSpeed;
	}
	public void setJogSpeed(int jogSpeed) {
		this.jogSpeed = jogSpeed;
	}
	public int getFirstSpeed() {
		return firstSpeed;
	}
	public void setFirstSpeed(int firstSpeed) {
		this.firstSpeed = firstSpeed;
	}
	public int getSecondSpeed() {
		return secondSpeed;
	}
	public void setSecondSpeed(int secondSpeed) {
		this.secondSpeed = secondSpeed;
	}
	public int getThirdSpeed() {
		return thirdSpeed;
	}
	public void setThirdSpeed(int thirdSpeed) {
		this.thirdSpeed = thirdSpeed;
	}
	public int getFourthSpeed() {
		return fourthSpeed;
	}
	public void setFourthSpeed(int fourthSpeed) {
		this.fourthSpeed = fourthSpeed;
	}
	public int getFifthSpeed() {
		return fifthSpeed;
	}
	public void setFifthSpeed(int fifthSpeed) {
		this.fifthSpeed = fifthSpeed;
	}
	public int getReturnSpeed() {
		return returnSpeed;
	}
	public void setReturnSpeed(int returnSpeed) {
		this.returnSpeed = returnSpeed;
	}
	public int getCompansateSpeed() {
		return compansateSpeed;
	}
	public void setCompansateSpeed(int compansateSpeed) {
		this.compansateSpeed = compansateSpeed;
	}
	public int getPressChangeSpeed() {
		return pressChangeSpeed;
	}
	public void setPressChangeSpeed(int pressChangeSpeed) {
		this.pressChangeSpeed = pressChangeSpeed;
	}
	public Speed(float prePress, int jogSpeed, int firstSpeed, int secondSpeed, int thirdSpeed, int fourthSpeed,
			int fifthSpeed, int returnSpeed, int compansateSpeed, int pressChangeSpeed) {
		super();
		this.prePress = prePress;
		this.jogSpeed = jogSpeed;
		this.firstSpeed = firstSpeed;
		this.secondSpeed = secondSpeed;
		this.thirdSpeed = thirdSpeed;
		this.fourthSpeed = fourthSpeed;
		this.fifthSpeed = fifthSpeed;
		this.returnSpeed = returnSpeed;
		this.compansateSpeed = compansateSpeed;
		this.pressChangeSpeed = pressChangeSpeed;
	}
	public Speed() {
		super();
	}
	@Override
	public String toString() {
		return "Speed [prePress=" + prePress + ", jogSpeed=" + jogSpeed + ", firstSpeed=" + firstSpeed
				+ ", secondSpeed=" + secondSpeed + ", thirdSpeed=" + thirdSpeed + ", fourthSpeed=" + fourthSpeed
				+ ", fifthSpeed=" + fifthSpeed + ", returnSpeed=" + returnSpeed + ", compansateSpeed=" + compansateSpeed
				+ ", pressChangeSpeed=" + pressChangeSpeed + "]";
	}
	
	
}
