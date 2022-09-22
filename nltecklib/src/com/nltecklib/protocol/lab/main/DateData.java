package com.nltecklib.protocol.lab.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class DateData extends Data implements Configable,Queryable,Responsable {
	
	private Date date;
	
	@Override
	public void encode() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		
		
		data.add((byte) (calendar.get(Calendar.YEAR) - 2000));
		data.add((byte) (calendar.get(Calendar.MONTH) + 1));
		data.add((byte) calendar.get(Calendar.DATE));
		data.add((byte) calendar.get(Calendar.HOUR_OF_DAY));
		data.add((byte) calendar.get(Calendar.MINUTE));
		data.add((byte) calendar.get(Calendar.SECOND));	
	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		Calendar calendar = Calendar.getInstance();
		
	
		int year = ProtocolUtil.getUnsignedByte(data.get(index++));
		int month = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (month < 1 || month > 12) {

			throw new RuntimeException("解析日期格式数据:月份必须在1-12之间");
		}
		int date = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (date < 1 || date > 31) {

			throw new RuntimeException("解析日期格式数据:日期必须在1-31之间");
		}
		int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (hour < 0 || hour > 23) {

			throw new RuntimeException("解析日期格式数据:小时必须在0-23之间");
		}
		int min = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (min < 0 || min > 59) {

			throw new RuntimeException("解析日期格式数据:分钟必须在0-59以内");
		}
		int second = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (second < 0 || second > 59) {

			throw new RuntimeException("解析日期格式数据:秒错误");
		}
		
		calendar.set(Calendar.YEAR, 2000 + year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, second);	
		this.date = calendar.getTime();
	}
	
	@Override
	public Code getCode() {
		return MainCode.DateCode;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}
	@Override
	public String toString() {
		return "DateData [date=" + date + "]";
	}
	
	
}
