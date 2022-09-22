package com.nltecklib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class LogUtil {

	private static Map<String, Logger> loggers = new HashMap<String, Logger>();

	private static Level defaultFileLevel = Level.DEBUG;
	private static Level defaultConsoleLevel = Level.ERROR;

	public static Level getDefaultFileLevel() {
		return defaultFileLevel;
	}

	public static void setDefaultFileLevel(Level defaultFileLevel) {
		LogUtil.defaultFileLevel = defaultFileLevel;
	}

	public static Level getDefaultConsoleLevel() {
		return defaultConsoleLevel;
	}

	public static void setDefaultConsoleLevel(Level defaultConsoleLevel) {
		LogUtil.defaultConsoleLevel = defaultConsoleLevel;
	}

	public static void clearLoggers() {
		loggers.clear();
	}

	/**
	 * 创建日志文件
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private static Logger createLog(String path, Level file, Level console, int maxBackupCount) throws Exception {

		Logger logger = Logger.getLogger(path);

		// file log;
		PatternLayout layout = new PatternLayout();
		String format = "%d{yyyy-MM-dd HH:mm:ss}  %p -%m%n";
		layout.setConversionPattern(format);
		// create appender;
		RollingFileAppender appenderFile = null;
		ConsoleAppender appenderConsole = null;

		appenderFile = new RollingFileAppender(layout, path);
		appenderConsole = new ConsoleAppender(layout);

		appenderConsole.setThreshold(console); // 当前显示
		appenderFile.setMaxBackupIndex(maxBackupCount);
		appenderFile.setThreshold(file);

		logger.addAppender(appenderFile);
		logger.addAppender(appenderConsole);

		return logger;
	}

	public static int getUnsignedByte(byte b) {

		return 0xff & b;
	}

	public static String printArray(byte[] data) {

		StringBuilder buff = new StringBuilder();
		for (byte b : data) {

			String str = Integer.toHexString(getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();
	}

	public static String printList(List<Byte> list) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : list) {

			String str = Integer.toHexString(getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();
	}

	/**
	 * 打印异常堆栈消息
	 * 
	 * @param e
	 * @return
	 */
	public static String printThrowable(Throwable e) {

		ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		e.printStackTrace(new java.io.PrintWriter(buf, true));
		String expMessage = buf.toString();
		try {
			buf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return expMessage;
	}

	/**
	 * 使用默认参数生成日志
	 * 
	 * @param label
	 * @return
	 * @throws Exception
	 */
	public static Logger getLogger(String label) {

		return getLogger(label, Level.INFO, Level.INFO, 30);
	}

	/**
	 * 
	 * @param label   日志名
	 * @param file    文件日志级别
	 * @param console 控制台日志级别
	 * @return
	 * @throws Exception
	 */
	public static Logger getLogger(String label, Level file, Level console, int maxBackupCount) {

		Logger logger = loggers.get(label);

		if (logger == null) {

			try {
				logger = LogUtil.createLog("log/" + label + ".log", file, console, maxBackupCount);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		loggers.put(label, logger);
		return logger;

	}

}
