package com.nltecklib.protocol.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ProtocolUtil {

	public static List<Byte> converString(String s) {

		List<Byte> bytes = new ArrayList<>();
		String[] arr = s.split(" ");
		for (int i = 0; i < arr.length; i++) {

			bytes.add((byte) Integer.parseInt(arr[i], 16));
		}

		return bytes;
	}

	public static Byte[] convertArrayType(byte[] data) {

		Byte[] d = new Byte[data.length];
		for (int i = 0; i < data.length; i++) {

			d[i] = data[i];
		}
		return d;

	}

	public static byte[] convertArrayType(Byte[] data) {

		byte[] d = new byte[data.length];
		for (int i = 0; i < data.length; i++) {

			d[i] = data[i];
		}
		return d;

	}

	public static List<Byte> convertArrayToList(byte[] data) {

		List<Byte> list = new ArrayList<Byte>(data.length);
		for (int n = 0; n < data.length; n++) {

			list.add(data[n]);
		}
		return list;
	}

	public static byte[] convertListToArray(List<Byte> list) {

		byte[] d = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {

			d[i] = list.get(i);
		}
		return d;
	}

	public static long compose(byte[] data, boolean descend) {

		return compose(convertArrayType(data), descend);
	}

	public static long compose(Byte[] data, boolean descend) {

		long val = 0;
		for (int i = 0; i < data.length; i++) {

			int n = descend ? i : data.length - 1 - i;

			// System.out.println(data[n] & 0xffl);

			val = (data[n] & 0xffl) << (data.length - 1 - i) * 8 | val;

		}

		return val;

	}

	public static int getUnsignedByte(byte index) {

		return 0xff & index;
	}

	/**
	 * 
	 * @param decade
	 *            10进制值转换为同等字面值得16进制值
	 * @return
	 */
	public static int decadeToHex(int decade) {

		return Integer.parseInt(decade + "", 16);
	}

	public static int hexToDecade(int hex) {

		return Integer.parseInt(Integer.toHexString(hex));
	}

	/**
	 * 将一个int值拆分成字节数组，len为拆分的字节个数,descend=true为高位在前，低位在后
	 * 
	 * @param val
	 * @param len
	 * @param descend
	 * @return
	 */
	public static Byte[] split(long val, int len, boolean descend) {

		Byte[] data = new Byte[len];
		for (int i = 0; i < len; i++) {

			int n = descend ? len - 1 - i : i;

			// System.out.println(Long.toHexString(0xffl << n * 8));

			long tmp = (val & 0xffl << n * 8) >>> n * 8 & 0xff;

			data[i] = (byte) tmp;

		}

		return data;

	}

	/**
	 * 特殊处理负数，把最高位置1表示负数，0表示正数
	 * 
	 * @param val
	 * @param len
	 * @param descend
	 * @param specialMinus
	 * @return
	 */
	public static Byte[] splitSpecialMinus(long val, int len, boolean descend) {

		if (val < 0) {

			long bit = 0x01L << (8 * len - 1);

			return split(Math.abs(val) | bit, len, descend);
		}

		return split(val, len, descend);

	}

	public static long composeSpecialMinus(Byte[] data, boolean descend) {

		boolean minus = false;
		// System.out.println(data[0] + " " + (data[0] & 0xff & 0x80));
		if (!descend) {

			//
			minus = (data[data.length - 1] & 0x80) > 0;
			data[data.length - 1] = (byte) (data[data.length - 1] & 0x7f);
		} else {

			minus = (data[0] & 0x80) > 0;
			data[0] = (byte) (data[0] & 0x7f);
		}

		return minus ? -compose(data, descend) : compose(data, descend);

	}

	public static long composeSpecialMinus(byte[] data, boolean descend) {

		return composeSpecialMinus(convertArrayType(data), descend);
	}

	/**
	 * 反向对字节进行位排序
	 * 
	 * @param data
	 * @param driverChnCount
	 *            驱动板通道数
	 * @return
	 */
	public static byte reverseByteBit(byte data, int driverChnCount) {

		if (driverChnCount > 8) {

			throw new RuntimeException("error driverChnCount " + driverChnCount + ",can not reverse byte bit flag");
		}

		String str = "00000000" + Integer.toBinaryString(data);
		str = str.substring(str.length() - 8, str.length());

		StringBuffer reverseStr = new StringBuffer();
		for (int i = str.length() - 1; i >= str.length() - driverChnCount; i--) {

			reverseStr.append(str.charAt(i));
		}
		return (byte) Integer.parseInt(reverseStr.toString(), 2);

	}

	/**
	 * 反向排序字节位
	 * 
	 * @param data
	 * @return
	 */
	public static byte reverseByteBit(byte data) {

		return reverseByteBit(data, 8);
	}

	// /**
	// * 根据总位数进行颠倒
	// * @param flag
	// * @param totalBit
	// * @return
	// */
	// public static int reverseByteBits(int flag) {
	//
	// int reverse = 0;
	// int val = flag & 0xff;
	//
	// for(int n = 0 ; n < 8 ; n++) {
	//
	// if((0x01 << n & val) > 0 ) {
	//
	// reverse |= 0x01 << (7 - n);
	// }
	// }
	//
	// return reverse;
	//
	//
	//
	// }
	/**
	 * 对short字节进行反位排序处理
	 * 
	 * @param data
	 * @param driverChnCount
	 * @return
	 */
	public static short reverseShortBit(short data, int driverChnCount) {

		if (driverChnCount > 16) {

			throw new RuntimeException("error driverChnCount " + driverChnCount + ",can not reverse short bit flag");
		}

		String str = new String("0000000000000000") + Integer.toBinaryString(data);
		str = str.substring(str.length() - 16, str.length());

		StringBuffer reverseStr = new StringBuffer();

		for (int i = str.length() - 1; i >= str.length() - driverChnCount; i--) {

			reverseStr.append(str.charAt(i));
		}
		return (short) Integer.parseInt(reverseStr.toString(), 2);
	}

	/**
	 * 对short字节进行反位排序处理
	 */
	public static short reverseShortBit(short data) {

		return reverseShortBit(data, 16);
	}

	/**
	 * 编码ip地址
	 * 
	 * @param ip
	 * @return
	 */
	public static List<Byte> encodeIp(String ip) {
		List<Byte> list = new ArrayList<>();
		String[] secs = ip.split("\\.");
		if (secs.length != 4) {

			throw new RuntimeException("the ip :" + ip + " is illegal");
		}
		for (int n = 0; n < secs.length; n++) {
			list.add((byte) Integer.parseInt(secs[n]));
		}
		return list;
	}

	/**
	 * 编码mac地址
	 * 
	 * @param mac
	 * @return
	 */
	public static List<Byte> encodeMac(String mac) {
		List<Byte> list = new ArrayList<>();
		String[] secs = mac.split(":");
		if (secs.length != 6) {

			throw new RuntimeException("the mac :" + mac + " is illegal");
		}
		for (int n = 0; n < secs.length; n++) {
			list.add((byte) Integer.parseInt(secs[n], 16));
		}
		return list;
	}

	/**
	 * 解码mac地址
	 * 
	 * @param list
	 * @return
	 */
	public static String decodeMac(List<Byte> list) {
		String mac = "";
		for (int n = 0; n < 6; n++) {
			int sec = ProtocolUtil.getUnsignedByte(list.get(n));
			mac += String.format("%02X", sec) + (n < 5 ? ":" : "");
		}
		return mac;
	}

	/**
	 * 解码ip地址
	 * 
	 * @param list
	 * @return
	 */
	public static String decodeIp(List<Byte> list) {
		String ip = "";
		for (int n = 0; n < 4; n++) {
			int sec = ProtocolUtil.getUnsignedByte(list.get(n));
			ip += sec + (n < 3 ? "." : "");
		}
		return ip;
	}

	/**
	 * 编码时间
	 * 
	 * @param date
	 * @param descend
	 *            true 按年月日时分秒 顺序编码;false 按秒分时日月年 顺序编码
	 * @return
	 */
	public static List<Byte> encodeDate(Date date, boolean descend) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		List<Byte> list = new ArrayList<>();

		// year
		list.add((byte) (c.get(Calendar.YEAR) - 2000));
		// month
		list.add((byte) (c.get(Calendar.MONTH) + 1));
		// day
		list.add((byte) (c.get(Calendar.DATE)));
		// hour
		list.add((byte) (c.get(Calendar.HOUR_OF_DAY)));
		// minute
		list.add((byte) (c.get(Calendar.MINUTE)));
		// second
		list.add((byte) (c.get(Calendar.SECOND)));

		if (!descend) {
			//反序
			java.util.Collections.reverse(list);
		}
		return list;

	}


	/**
	 * 解码时间
	 * 
	 * @param copyList
	 *            源字节数据
	 * @param descend
	 *            true 按年月日时分秒 顺序解码;false 按秒分时日月年 顺序解码
	 * @return
	 */
	public static Date decodeDate(List<Byte> list, boolean descend) {

		int index = 0;
		Calendar calendar = Calendar.getInstance();
		int year = 0, month = 0, date = 0;
		int hour = 0, minute = 0, second = 0;
		
		// 因为要反序，怕影响到原来的报文
		List<Byte> copyList = new ArrayList<>(list);

		if (!descend) {
			java.util.Collections.reverse(copyList);
		}

		year = ProtocolUtil.getUnsignedByte(copyList.get(index++));

		month = ProtocolUtil.getUnsignedByte(copyList.get(index++));
		if (month < 1 || month > 12) {

			throw new RuntimeException("解析日期格式数据:月份必须在1-12之间");
		}

		date = ProtocolUtil.getUnsignedByte(copyList.get(index++));
		if (date < 1 || date > 31) {

			throw new RuntimeException("解析日期格式数据:日期必须在1-31之间");
		}

		hour = ProtocolUtil.getUnsignedByte(copyList.get(index++));
		if (hour < 0 || hour > 23) {

			throw new RuntimeException("解析日期格式数据:小时必须在0-23之间");
		}

		minute = ProtocolUtil.getUnsignedByte(copyList.get(index++));
		if (minute < 0 || minute > 59) {

			throw new RuntimeException("解析日期格式数据:分钟必须在0-59以内");
		}

		second = ProtocolUtil.getUnsignedByte(copyList.get(index++));
		if (second < 0 || second > 59) {

			throw new RuntimeException("解析日期格式数据:秒必须在0-59以内");
		}

		calendar.set(Calendar.YEAR, 2000 + year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTime();
	}

	/**
	 * 将分区内的通道序号按一定规则进行反序处理
	 * 
	 * @param chnIndexInLogic
	 * @param chnCountInDriver
	 * @return
	 */
	public static int reverseChnIndexInLogic(int chnIndexInLogic, int chnCountInDriver) {

		int driverIndex = chnIndexInLogic / chnCountInDriver;
		int chnIndexInDriver = chnIndexInLogic % chnCountInDriver;
		return driverIndex * chnCountInDriver + chnCountInDriver - 1 - chnIndexInDriver;

	}

	/**
	 * 将字符串编码成字节码流
	 * 
	 * @param list
	 * @param content
	 * @param charset
	 * @param len
	 *            按最长长度截断，多余补0 ; 0表示不限制
	 */
	public static List<Byte> encodeString(String content, String charset, int len) {

		byte[] array = null;
		try {
			array = content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		List<Byte> list = new ArrayList<Byte>();
		if (len > 0) {
			for (int i = 0; i < len; i++) {

				if (i < array.length) {

					list.add(array[i]);
				} else {

					list.add((byte) 0);
				}
			}
		} else {

			for (int i = 0; i < array.length; i++) {

				list.add(array[i]);
			}
		}

		return list;
	}

	/**
	 * 将字节码按指定字符编码解码成字符串
	 * 
	 * @param data
	 * @param start
	 *            开始解码起始位
	 * @param len
	 *            要解码的字节码长度
	 * @param charset
	 *            字符编码
	 * @return
	 */
	public static String decodeString(List<Byte> data, int start, int len, String charset) {

		byte[] nameBytes = new byte[len];

		int strLen = 0;
		for (int i = start; i < start + len; i++) {

			if (data.get(i) == 0) {

				break;
			}
			nameBytes[i - start] = data.get(i);
			strLen++;

		}
		String str = "";
		try {

			str = new String(Arrays.copyOfRange(nameBytes, 0, strLen), charset);
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 以十六进制打印 ByteList
	 * 
	 * @param list
	 * @return
	 */
	public static String printList(List<Byte> list) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : list) {

			String str = Integer.toHexString(ProtocolUtil.getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();

	}

	public static List<Byte> encodeUuid(String uuid) {
		List<Byte> list = new ArrayList<Byte>();
		int index = 0;
		for (int i = 0; i < uuid.length() / 2; i++) {

			list.add((byte) Integer.parseInt(uuid.substring(index, index = index + 2), 16));
		}
		return list;
	}

	public static String decodeUuid(List<Byte> data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			String string = Integer.toHexString(ProtocolUtil.getUnsignedByte(data.get(i)));
			if (string.length() < 2) {
				string = "0" + string;
			}
			stringBuilder.append(string);
		}
		return stringBuilder.toString();

	}
}
