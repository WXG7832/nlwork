package com.nltecklib.protocol.li.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class DateData extends Data implements Configable,Queryable,Responsable{

	private Date date;

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void encode() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		data.add((byte) calendar.get(Calendar.SECOND));
		data.add((byte) calendar.get(Calendar.MINUTE));
		data.add((byte) calendar.get(Calendar.HOUR_OF_DAY));
		data.add((byte) calendar.get(Calendar.DATE));
		data.add((byte) (calendar.get(Calendar.MONTH) + 1));
		data.add((byte) (calendar.get(Calendar.YEAR) - 2000));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
        
		int index = 0;
		Calendar calendar = Calendar.getInstance();
		int second = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (second < 0 || second > 59) {

			throw new RuntimeException("second must be in 0-59");
		}
		calendar.set(Calendar.SECOND, second);

		int min = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (min < 0 || min > 59) {

			throw new RuntimeException("minute must be in 0-59");
		}

		int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (hour < 0 || hour > 23) {

			throw new RuntimeException("hour must be in 0-23");
		}

		int date = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (date < 1 || date > 31) {

			throw new RuntimeException("date must be in 1-31");
		}

		int month = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (month < 1 || month > 12) {

			throw new RuntimeException("month must be in 1-12");
		}

		int year = ProtocolUtil.getUnsignedByte(data.get(index++));

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

		return this.date;
	}

	@Override
	public String toString() {
		return "DateData [date=" + date + "]";
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

}
