package com.nlteck.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProtocolUtil {
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

			int bit =  0x01 << (8 * len - 1);

			return split(Math.abs(val) | bit, len, descend);
		}

		return split(val, len, descend);

	}

	public static long composeSpecialMinus(Byte[] data, boolean descend) {

		boolean minus = false;
		//System.out.println(data[0] + "  " + (data[0] & 0xff & 0x80));
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
	 * 根据总位数进行颠倒
	 * @param flag
	 * @param totalBit
	 * @return
	 */
	public static int reverseByteBits(int flag) {
		
		int reverse = 0;
		int val = flag & 0xff;
		
		for(int n = 0 ; n < 8 ; n++) {
			
			if((0x01 << n &  val) > 0 ) {
				
				reverse |= 0x01 << (7 - n);
			}
		}
		
		return reverse;
		
		
		
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

		List<Byte> list = new ArrayList<Byte>(6);

		if (descend) {
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
		} else {

			// second
			list.add((byte) (c.get(Calendar.SECOND)));
			// minute
			list.add((byte) (c.get(Calendar.MINUTE)));
			// hour
			list.add((byte) (c.get(Calendar.HOUR_OF_DAY)));
			// day
			list.add((byte) (c.get(Calendar.DATE)));
			// month
			list.add((byte) (c.get(Calendar.MONTH) + 1));
			// year
			list.add((byte) (c.get(Calendar.YEAR) - 2000));

		}

		return list;

	}
    /**
     * 解码时间
     * @param list  源字节数据
     * @param descend  true 按年月日时分秒 顺序解码;false 按秒分时日月年 顺序解码
     * @return
     */
	public static Date decodeDate(List<Byte> list, boolean descend) {

		int index = 0;
		Calendar calendar = Calendar.getInstance();
		int year = 0, month = 0, date = 0;
		int hour = 0, minute = 0, second = 0;
		if (descend) {
           
			year = ProtocolUtil.getUnsignedByte(list.get(index++));
			
			month = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (month < 1 || month > 12) {

				throw new RuntimeException("解析日期格式数据:月份必须在1-12之间");
			}
			
			date = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (date < 1 || date > 31) {

				throw new RuntimeException("解析日期格式数据:日期必须在1-31之间");
			}
			
			hour = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (hour < 0 || hour > 23) {

				throw new RuntimeException("解析日期格式数据:小时必须在0-23之间");
			}
			
			minute = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (minute < 0 || minute > 59) {

				throw new RuntimeException("解析日期格式数据:分钟必须在0-59以内");
			}
			
			second = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (second < 0 || second > 59) {

				throw new RuntimeException("解析日期格式数据:秒错误");
			}
			calendar.set(Calendar.SECOND, second);
			
			
		} else {
			second = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (second < 0 || second > 59) {

				throw new RuntimeException("解析日期格式数据:秒错误");
			}
			calendar.set(Calendar.SECOND, second);

			minute = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (minute < 0 || minute > 59) {

				throw new RuntimeException("解析日期格式数据:分钟必须在0-59以内");
			}

			hour = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (hour < 0 || hour > 23) {

				throw new RuntimeException("解析日期格式数据:小时必须在0-23之间");
			}

			date = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (date < 1 || date > 31) {

				throw new RuntimeException("解析日期格式数据:日期必须在1-31之间");
			}

			month = ProtocolUtil.getUnsignedByte(list.get(index++));
			if (month < 1 || month > 12) {

				throw new RuntimeException("解析日期格式数据:月份必须在1-12之间");
			}

			year = ProtocolUtil.getUnsignedByte(list.get(index++));
		}

		calendar.set(Calendar.YEAR, 2000 + year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTime();
	}

}
