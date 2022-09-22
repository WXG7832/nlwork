package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class SlpProtectData extends Data implements Configable,Queryable,Responsable,Cloneable {
	
	/**
	 * 电压跳动超差值mV
	 */
	private double  voltOffset;
	/**
	 * 电压上限mV
	 */
	private double  voltUpper;
	/**
	 * 电压下限mV
	 */
	private double  voltLower;
	/**
	 * 电流上限mA
	 */
	private double  currUpper; 
	
	@Override
	public void encode() {
		
		//电压跳动差值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)voltOffset, 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 10), 3,true)));
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
		voltOffset = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		voltUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currUpper = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
	}
	
	@Override
	public Code getCode() {
		return MainCode.SleepProtectCode;
	}
	
	
	public double getVoltOffset() {
		return voltOffset;
	}

	public void setVoltOffset(double voltOffset) {
		this.voltOffset = voltOffset;
	}


	@Override
	public boolean supportChannel() {
		return true;
	}
	
	
	
	
	public double getCurrUpper() {
		return currUpper;
	}
	public void setCurrUpper(double currUpper) {
		this.currUpper = currUpper;
	}
	public double getVoltUpper() {
		return voltUpper;
	}
	public void setVoltUpper(double voltUpper) {
		this.voltUpper = voltUpper;
	}
	public double getVoltLower() {
		return voltLower;
	}
	public void setVoltLower(double voltLower) {
		this.voltLower = voltLower;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	@Override
	public String toString() {
		return "SlpProtectData [voltOffset=" + voltOffset + ", voltUpper=" + voltUpper + ", voltLower=" + voltLower
				+ ", currUpper=" + currUpper + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SlpProtectData)) {
			return false;
		}
		SlpProtectData other = (SlpProtectData) obj;
		if (Double.doubleToLongBits(currUpper) != Double.doubleToLongBits(other.currUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(voltLower) != Double.doubleToLongBits(other.voltLower)) {
			return false;
		}
		if (Double.doubleToLongBits(voltOffset) != Double.doubleToLongBits(other.voltOffset)) {
			return false;
		}
		if (Double.doubleToLongBits(voltUpper) != Double.doubleToLongBits(other.voltUpper)) {
			return false;
		}
		return true;
	}
	
	
	
	
}
