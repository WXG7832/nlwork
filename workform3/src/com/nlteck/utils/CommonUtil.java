package com.nlteck.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	public final static String PATTERN_IP = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$";

	public static boolean checkDigit(String context, boolean decimal) {

		/*
		 * if(context == null || context.isEmpty()) return false;
		 */

		char[] chars = new char[context.length()];

		context.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!('0' <= chars[i] && chars[i] <= '9') && chars[i] != 8) {

				if (decimal && chars[i] == 46) {
					continue;
				}

				return false;
			}
		}

		return true;

	}

	public static String getThrowableException(Throwable e) {

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
	 * 特殊处理电流电压值
	 * 
	 * @param programVal
	 * @param val
	 * @param offRange
	 * @return
	 */
	public static double specialProcessData(double programVal, double val, double offRange) {

		double offset = Math.abs(programVal - val);
		if (offset <= offRange) {

			val = programVal + (val - programVal) / 4;

		}
		return val;
	}

	public static boolean isNullOrEmpty(String str) {

		if (str == null)
			return true;
		if (str.isEmpty())
			return true;

		return false;
	}

	public static boolean checkIP(String input) {

		Pattern p = Pattern.compile(PATTERN_IP);
		Matcher m = p.matcher(input);
		return m.matches();
	}

	/**
	 * 
	 * @param one
	 *            被比较对象，可以是null
	 * @param another
	 *            要比较对象，可以是null
	 * @return 两个null对象都为null则返回true
	 */
	public static boolean equals(Object one, Object another) {

		if (one != null) {

			return one.equals(another);
		} else if (another != null) {
			return another.equals(one);
		} else {

			return true; // 两个都为空
		}
	}

	public static String formatTime(long seconds) {

		int hour = (int) (seconds / 3600);
		int min = (int) ((seconds % 3600) / 60);
		int sec = (int) (seconds % 3600 % 60);

		StringBuilder buff = new StringBuilder();
		if (hour < 10)
			buff.append("0");
		buff.append(hour);
		buff.append(":");
		if (min < 10)
			buff.append("0");
		buff.append(min);
		buff.append(":");
		if (sec < 10)
			buff.append("0");
		buff.append(sec);

		return buff.toString();

	}

	/**
	 * 
	 * @param decimalCount
	 *            小数位个数
	 * @return
	 */
	public static double getDouble(double val, int decimalCount) {

		return Math.round(val * Math.pow(10, decimalCount)) / Math.pow(10, decimalCount);
	}

	public static void sleep(int delay) {

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 格式化数字显示
	 * 
	 * @param val
	 * @param decimal
	 *            ; 小数位
	 * @return
	 */
	public static String formatNumber(double val, int decimal) {

		String format = "0";
		if (decimal > 0) {

			format += ".";
			for (int i = 0; i < decimal; i++) {

				format += "0";
			}
		}

		return new DecimalFormat(format).format(val);
	}

	public static String formatTime(Date date, String pattern) {

		if (date == null)
			return "";
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.format(date);

	}

	public static String formatTime(Date date, String pattern, String zone) {

		if (date == null)
			return "";
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		sf.setTimeZone(TimeZone.getTimeZone(zone));
		return sf.format(date);

	}

	/**
	 * 转成其他时区
	 * 
	 * @param date
	 * @param zone
	 * @return
	 * @throws ParseException
	 */
	public static Date convertToZone(Date date, String zone) throws ParseException {

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sf.setTimeZone(TimeZone.getTimeZone(zone));
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sf.format(date));
	}

	public static Date parseTime(String text, String pattern) throws ParseException {

		if (text == null || text.isEmpty()) {
			return null;
		}
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.parse(text);

	}

	public static String getUUID() {

		return UUID.randomUUID().toString().replaceAll("-", "");

	}

	/**
	 * 取一定范围内的随机数
	 * 
	 * @param base
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static double produceRandomValue(double base, double lower, double upper) {

		Random random = new Random();
		int range = (int) (upper * 10 - lower * 10);
		return (double) random.nextInt(range) / 10 + lower;
	}

	/**
	 * 取浮点型的整数部分，如果有小数则整数部分+1
	 * 
	 * @param val
	 * @return
	 */
	public static int getIntegerFromVal(double val) {

		return val % 1 == 0 ? (int) val : (int) (val + 1);
	}

	/**
	 * 替换队列中的元素
	 * 
	 * @param source
	 * @param newList
	 */
	public static void changeQueueValues(List<Byte> source, List<Byte> newList) {

		for (int n = 0; n < source.size(); n++) {

			source.set(n, newList.get(n));
		}
	}
    /**
     * 递归删除所有子文件夹及子文件
     * @param file
     * @return
     */
	public static boolean deleteFile(File file) {
		if (!file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFile(f);
			}
		}
		return file.delete();
	}
	
	/**
	 * 产生一定范围的随机数
	 * @param base
	 * @param offset
	 * @return
	 */
	public static double produceRandomNumberInRange(double base , double range) {
		
		 double offset = new Random().nextDouble() * range * 2 - range;
		 return base + offset;
	}

}
