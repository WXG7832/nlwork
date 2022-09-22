package com.nlteck.model;

import java.util.Date;

public class CalData {
    private int id;
    private int testId;
    private int logicIndex;
    private int driverIndex;
    private int channelIndex;
    private int calibrateIndex;
    private String state;
    private String mode;
    private String pole;
    private int level;
    private int step;
    private int totalStep;
    private double calDot;
    private double adc;
    private double meter;
    private double elapseSeconds;
    private Date date;

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public int getId() {
	return id;
    }

    public int getDriverIndex() {
	return driverIndex;
    }

    public void setDriverIndex(int driverIndex) {
	this.driverIndex = driverIndex;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getTestId() {
	return testId;
    }

    public void setTestId(int testId) {
	this.testId = testId;
    }

    public int getLogicIndex() {
	return logicIndex;
    }

    public void setLogicIndex(int logicIndex) {
	this.logicIndex = logicIndex;
    }

    public int getChannelIndex() {
	return channelIndex;
    }

    public void setChannelIndex(int channelIndex) {
	this.channelIndex = channelIndex;
    }

    public int getCalibrateIndex() {
	return calibrateIndex;
    }

    public void setCalibrateIndex(int calibrateIndex) {
	this.calibrateIndex = calibrateIndex;
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

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }

    public int getStep() {
	return step;
    }

    public void setStep(int step) {
	this.step = step;
    }

    public int getTotalStep() {
	return totalStep;
    }

    public void setTotalStep(int totalStep) {
	this.totalStep = totalStep;
    }

    public double getCalDot() {
	return calDot;
    }

    public void setCalDot(double calDot) {
	this.calDot = calDot;
    }

    public double getAdc() {
	return adc;
    }

    public void setAdc(double adc) {
	this.adc = adc;
    }

    public double getMeter() {
	return meter;
    }

    public void setMeter(double meter) {
	this.meter = meter;
    }

    public double getElapseSeconds() {
	return elapseSeconds;
    }

    public void setElapseSeconds(double elapseSeconds) {
	this.elapseSeconds = elapseSeconds;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

}
