package com.nlteck.model;

import java.util.Date;

public class TestData {
    private int id;
    private String testName;
    private String testDevice;
    private Date startTime;
    private Date endTime;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getTestName() {
	return testName;
    }

    public void setTestName(String testName) {
	this.testName = testName;
    }

    public String getTestDevice() {
	return testDevice;
    }

    public void setTestDevice(String testDevice) {
	this.testDevice = testDevice;
    }

    public Date getStartTime() {
	return startTime;
    }

    public void setStartTime(Date startTime) {
	this.startTime = startTime;
    }

    public Date getEndTime() {
	return endTime;
    }

    public void setEndTime(Date endTime) {
	this.endTime = endTime;
    }

}
