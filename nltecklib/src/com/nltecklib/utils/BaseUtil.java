package com.nltecklib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础工具
 * 
 * @author Administrator
 *
 */
public class BaseUtil {

	public final static String PATTERN_IP = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$";
	public final static String MAC_PATTERN = "^([0-9a-fA-F]{2})((:[0-9a-fA-F]{2}){5})$";

	/**
	 * 检验IP地址格式是否正确
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean checkIpAddress(String ip) {

		Pattern p = Pattern.compile(PATTERN_IP);
		Matcher m = p.matcher(ip);
		return m.matches();
	}

	/**
	 * 打印异常堆栈错误信息
	 * 
	 * @param cause
	 * @return
	 */
	public static String getThrowableException(Throwable cause) {

		ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		cause.printStackTrace(new java.io.PrintWriter(buf, true));
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
	 * 检验MAC地址格式是否正确，必须以:作为分隔符
	 * 
	 * @param mac
	 * @return
	 */
	public static boolean checkMacAddress(String mac) {

		return Pattern.matches(MAC_PATTERN, mac);
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

	public static void sleep(long sleepTime) {

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String formatDate(String format, Date date) {
        
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(date);
	}
	
	public static Date parseDate(String format , String dateStr) throws ParseException {
		
		if (dateStr == null || dateStr.isEmpty()) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateStr);
	}

	public static String formatSeconds(int seconds) {

		int sec = seconds % 60;
		int min = seconds / 60 % 60;
		int hour = seconds / 3600;

		String str = "";
		if (hour > 0) {
			str = String.format("%02d:%02d:%02d", hour, min, sec);
		} else {
            
			str = String.format("%02d:%02d", min, sec);
		}
		
		return str;

	}
	

}
