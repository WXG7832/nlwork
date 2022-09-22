package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

//校准板地址
public class CalAddressData extends Data implements Queryable, Configable, Responsable {
  
	private int calGrade;

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

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		data.add((byte) calGrade); // 校准板标号
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		calGrade = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalEnvironment.CalCode.ADDRESS_CAL;
	}

	public int getCalGrade() {
		return calGrade;
	}

	public void setCalGrade(int calGrade) {
		this.calGrade = calGrade;
	}

	@Override
	public String toString() {
		return "CalAddressData [calGrade=" + calGrade + "]";
	}
	
	
	
}
