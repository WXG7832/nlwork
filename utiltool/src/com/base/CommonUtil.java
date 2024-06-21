package com.base;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.nltecklib.utils.ZipUtil;

public class CommonUtil {

	public final static String PATTERN_IP = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$";

	/**
	 * ����ַ����Ƿ������ֹ���     decimalС������
	 * @param context
	 * @param decimal
	 * @return
	 */
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
			e1.printStackTrace();
		}

		return expMessage;

	}

	/**
	 * ��XML��ʽ�ĵ�����ѹ������
	 * 
	 * @author wavy_zheng 2023��3��6��
	 * @param path
	 * @param document
	 * @throws Exception
	 */
	public static void saveCompressXml(String path, Document document) throws Exception {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		OutputFormat format = OutputFormat.createCompactFormat();
		XMLWriter xmlWriter = new XMLWriter(outputStream, format);
		xmlWriter.write(document);
		xmlWriter.close();
		byte[] data = outputStream.toByteArray();
		// ѹ��
		byte[] compressData = ZipUtil.compress(data);

		FileOutputStream foutputStream = new FileOutputStream(new File(path));
		foutputStream.write(compressData);
		foutputStream.close();

	}

	
	/**
	 * ���⴦�������ѹֵ
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

	/**
	 * ���ip
	 */
	public static boolean checkIP(String input) {

		Pattern p = Pattern.compile(PATTERN_IP);
		Matcher m = p.matcher(input);
		return m.matches();
	}

	/**
	 * 
	 * @param one
	 *            ���Ƚ϶��󣬿�����null
	 * @param another
	 *            Ҫ�Ƚ϶��󣬿�����null
	 * @return ����null����Ϊnull�򷵻�true
	 */
	public static boolean equals(Object one, Object another) {

		if (one != null) {

			return one.equals(another);
		} else if (another != null) {
			return another.equals(one);
		} else {

			return true; // ������Ϊ��
		}
	}

	/**
	 * ��ʽ��ʱ��
	 * @param seconds
	 * @return
	 */
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
	 *            С��λ����
	 * @return
	 */
	public static double getDouble(double val, int decimalCount) {

		return Math.round(val * Math.pow(10, decimalCount)) / Math.pow(10, decimalCount);
	}

	/**
	 * ����
	 * @param delay
	 */
	public static void sleep(int delay) {

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʽ��������ʾ
	 * 
	 * @param val
	 * @param decimal
	 *            ; С��λ
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

	/**
	 * ��pattern ��ʽ��ʱ��
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatTime(Date date, String pattern) {

		if (date == null)
			return "";
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.format(date);

	}

	/**
	 * ��ʱ����ʽ��ʱ��
	 * @param date
	 * @param pattern
	 * @param zone
	 * @return
	 */
	public static String formatTime(Date date, String pattern, String zone) {

		if (date == null)
			return "";
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		sf.setTimeZone(TimeZone.getTimeZone(zone));
		return sf.format(date);

	}

	/**
	 * ת������ʱ��
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
	 * ȡһ����Χ�ڵ������
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
	 * ȡ�����͵��������֣������С������������+1
	 * 
	 * @param val
	 * @return
	 */
	public static int getIntegerFromVal(double val) {

		return val % 1 == 0 ? (int) val : (int) (val + 1);
	}

	/**
	 * �滻�����е�Ԫ��
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
	 * 
	 * @Description: �����ļ�
	 *
	 */
	public static void copyFileUsingStream(File source, File dest) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}
	
	/**
	 * �ݹ�ɾ���������ļ��м����ļ�
	 * 
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
	 * ����һ����Χ�������
	 * 
	 * @param base
	 * @param offset
	 * @return
	 */
	public static double produceRandomNumberInRange(double base, double range) {

		double offset = new Random().nextDouble() * range * 2 - range;
		return base + offset;
	}


    

}
