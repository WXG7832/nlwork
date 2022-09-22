package com.nlteck.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
	
	public static String formatMinute(long seconds) {
		
		int hour = (int) (seconds / 3600);
		int min = (int) ((seconds % 3600) / 60);
		int sec = (int) (seconds % 3600 % 60);
		
		StringBuilder buff = new StringBuilder();
		if (min < 10) {
			buff.append("0");
		}
		buff.append(min);
		buff.append(":");
		if (sec < 10) {
			buff.append("0");
		}
		buff.append(sec);

		return buff.toString();
		
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

	public static String formatTimeExceptHours(long seconds) {
		int min = (int) (seconds / 60);
		int sec = (int) (seconds % 60);

		StringBuilder buff = new StringBuilder();
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
	 * 分解SNAP 和 stepId
	 * 
	 * @param input
	 * @param snap
	 * @return -1不匹配
	 */
	public static int parseCapcityStep(String input, StringBuffer snap) {

		Pattern pattern = Pattern.compile("(Cdc|Ccv|Ccc)([1-9])?", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {

			// System.out.println(matcher.group(1));
			snap.append(matcher.group(1));
			return CommonUtil.isNullOrEmpty(matcher.group(2)) ? 0 : Integer.parseInt(matcher.group(2));
		}

		return -1;
	}

	// public static String getNameFromSNAP(SNAP snap) {
	//
	// switch (snap) {
	// case START_TIME:
	// return "流程起始时间";
	// case END_TIME:
	// return "流程结束时间";
	// case CAPACITY_CC:
	// return "CC步次容量(mAh)";
	// case CAPACITY_CV:
	// return "CV步次容量(mAh)";
	// case CAPACITY_DC:
	// return "DC步次容量(mAh)";
	// case START_VOLTAGE:
	// return "流程起始电压(mV)";
	// case END_VOLTAGE:
	// return "流程结束电压(mV)";
	// case START_CURRENT:
	// return "流程起始电流(mA)";
	// case END_CURRENT:
	// return "流程结束电流(mA)";
	// case CURRENT_CV:
	// return "CV步次结束电流(mA)";
	// case VOLTAGE_CC:
	// return "CC步次结束电压(mV)";
	// case VOLTAGE_DC:
	// return "DC步次结束电压(mV)";
	// }
	// return "";
	// }

	public static String formatTime(Date date, String pattern) {

		if (date == null) {
			return "";
		}
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.format(date);

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

	public static boolean isEmptyOrZero(String val) {

		if (val == null)
			return true;
		if (val.isEmpty())
			return true;
		if (Integer.parseInt(val) == 0)
			return true;

		return false;
	}

	public static void sleep(int timeout) {

		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String formatNumber(Number number) {

		if (number == null)
			return "";
		return number.toString();
	}

	public static String formatNumberZeroToEmpty(Number number) {

		if (number == null || number.intValue() == 0)
			return "";
		return number.toString();
	}

	public static void swap(Object[] array, int a, int b) {

		Object tmp = array[a];
		array[a] = array[b];
		array[b] = tmp;
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

	private static double smoothNextDot(double base, double currentDot, double k) {

		return base * (1 - k) + currentDot * k;
	}

	/**
	 * 判断两个list是否相等
	 * 
	 * @param firstList
	 * @param secondList
	 * @return
	 */
	public static boolean equalList(List firstList, List secondList) {

		if (firstList.size() != secondList.size()) {

			return false;
		}
		return Arrays.deepEquals(firstList.toArray(), secondList.toArray());

	}

	/**
	 * 将字符串首字母改成大写
	 * 
	 * @param name
	 * @return
	 */
	public static String firstToUpperCase(String name) {
		// name = name.substring(0, 1).toUpperCase() + name.substring(1);
		// return name;
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);

	}

	/**
	 * 将字符串首字母改成小写
	 * 
	 * @param name
	 * @return
	 */
	public static String firstToLowerCase(String name) {
		char[] cs = name.toCharArray();
		cs[0] += 32;
		return String.valueOf(cs);

	}

	/**
	 * 执行一个线程直到返回
	 * 
	 * @param runnable
	 * @return null表示成功；不为null表示返回失败，获取失败原因
	 */
	public static String executeThreadUtilResult(Callable<String> callable) {

		// CyclicBarrier barrier = new CyclicBarrier(1);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> result = executor.submit(callable);
		try {
			return result.get(); // 等待返回线程运行结果
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "线程执行被打断";
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "线程执行异常:" + e.getMessage();
		}
	}

	public static double round(double val, int exp) {
		return (double) Math.round(val * (Math.pow(10, exp))) / Math.pow(10, exp);
	}

	/**
	 * 把一个十进制整数转为规定长度（高位补0）的二进制List，且反序输出
	 * 
	 * @param num
	 * @param lenth
	 * @return
	 */
	public static List<Integer> numToBinaryIntList(int num, int lenth) {
		char[] chars = Integer.toBinaryString(num).toCharArray();
		List<String> stringList = new ArrayList<>();
		for (int i = 0; i < chars.length; i++)
			stringList.add(chars[chars.length - 1 - i] + "");
		int size = stringList.size();
		if (size <= lenth) {
			for (int i = 0; i < lenth - size; i++)
				stringList.add(0 + "");
		} else
			stringList = stringList.subList(0, lenth);
		List<Integer> intList = new ArrayList<>();
		for (String str : stringList)
			intList.add(Integer.parseInt(str));
		return intList;
	}

}
