package com.nlteck.model;

import java.util.Date;

import com.nlteck.firmware.Device;

public class TestLog {
	private int id;
	private String level;
	private String content;
	private int deviceChnIndex;
	private Device device;
	private Date date;

	public TestLog() {
		super();
	}

	public TestLog(int deviceChnIndex, String level, String content, Date date) {
		super();
		
		this.deviceChnIndex = deviceChnIndex;
		this.level = level;
		this.content = content;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDeviceChnIndex() {
		return deviceChnIndex;
	}

	public void setDeviceChnIndex(int deviceChnIndex) {
		this.deviceChnIndex = deviceChnIndex;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	@Override
	public String toString() {
		return "TestLog [id=" + id + ", level=" + level + ", content=" + content + ", deviceChnIndex=" + deviceChnIndex
				+ ", date=" + date + "]";
	}
	
	

}
