package com.nlteck.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogItem {
	// private static final SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd
	// HH:mm:ss");
	private static final SimpleDateFormat SF = new SimpleDateFormat("HH:mm:ss");

	public String deviceName;
	public String testName;
	public int channel;
	public Date time;
	public LogLevel logLevel;
	public String logMsg;

	public enum LogLevel {
		Normal, Error
	}

	public LogItem(String deviceName, String testName, int chn, Date time, LogLevel logLevel, String logMsg) {
		this.deviceName = deviceName;
		this.testName = testName;
		this.channel = chn;
		this.time = time;
		this.logLevel = logLevel;
		this.logMsg = logMsg;
	}

	public LogItem() {

	}

	@Override
	public String toString() {
		return SF.format(time) + "  " + logMsg;
	}
}
