package com.nlteck.model;

public class MeasureDotDO {

	private int id;
	private int chnId;
	private String mode;
	private String pole;
	private double calculateDot;
	private double finalAdc;
	private int    level;
	private double meterVal;
	private String result;
	private String info;
	private int    index;

	private ChannelDO  channel;
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getChnId() {
		return chnId;
	}
	public void setChnId(int chnId) {
		this.chnId = chnId;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getPole() {
		return pole;
	}
	public void setPole(String pole) {
		this.pole = pole;
	}
	public double getCalculateDot() {
		return calculateDot;
	}
	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}
	public double getFinalAdc() {
		return finalAdc;
	}
	public void setFinalAdc(double finalAdc) {
		this.finalAdc = finalAdc;
	}
	public double getMeterVal() {
		return meterVal;
	}
	public void setMeterVal(double meterVal) {
		this.meterVal = meterVal;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public ChannelDO getChannel() {
		return channel;
	}
	public void setChannel(ChannelDO channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		return "MeasureDot [id=" + id + ", chnId=" + chnId + ", mode=" + mode + ", pole=" + pole + ", calculateDot="
				+ calculateDot + ", finalAdc=" + finalAdc + ", meterVal=" + meterVal + ", result=" + result + ", info="
				+ info + "]";
	}
	
}
