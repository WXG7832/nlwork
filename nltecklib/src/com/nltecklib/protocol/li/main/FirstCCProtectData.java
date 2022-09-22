package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class FirstCCProtectData extends Data implements Configable, Queryable, Responsable {
    
	private boolean  needCheck;
	private double   voltLower;
	private double   voltUpper;
	private int      timeOut;  //在规定时间范围内电压范围
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		 data.add((byte)unitIndex);
		 data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2, true)));
		 data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2, true)));
		 data.addAll(Arrays.asList(ProtocolUtil.split((long)timeOut, 2, true)));
		 data.add(needCheck ? (byte)1 : (byte)0);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
	    data = encodeData;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    voltUpper  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	    voltLower  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	    timeOut  = (int) ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
	    index += 2;
	    needCheck = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.FirstCCProtectCode;
	}

	public boolean isNeedCheck() {
		return needCheck;
	}

	public void setNeedCheck(boolean needCheck) {
		this.needCheck = needCheck;
	}

	public double getVoltLower() {
		return voltLower;
	}

	public void setVoltLower(double voltLower) {
		this.voltLower = voltLower;
	}

	public double getVoltUpper() {
		return voltUpper;
	}

	public void setVoltUpper(double voltUpper) {
		this.voltUpper = voltUpper;
	}
	
	

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public String toString() {
		return "FirstCCProtectData [needCheck=" + needCheck + ", voltLower=" + voltLower + ", voltUpper=" + voltUpper
				+ ", timeOut=" + timeOut + "]";
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof FirstCCProtectData){
			
			FirstCCProtectData fpd = (FirstCCProtectData) obj;
			if (fpd.getUnitIndex() == this.unitIndex 
					&& (fpd.isNeedCheck() == this.needCheck)
					&& fpd.getVoltLower() == this.voltLower
					&& fpd.getVoltUpper() == this.voltUpper
					&& fpd.getTimeOut() == this.timeOut) {
				
				return true;
			}
		}
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
