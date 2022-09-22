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

public class ChnFirstLevelProtectData extends Data implements Configable,Queryable,Responsable,Cloneable {

	
	private double firstLevelOverVolt;
	private double secondLevelOverVolt;
	private double overCurr;
	private double chnTempUpper;
	private double chnTempLower;
	private boolean enableTempProtect;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(firstLevelOverVolt * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(secondLevelOverVolt * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(overCurr * 10), 3,true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)(chnTempUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)(chnTempLower * 10), 2,true)));
		
		if(Data.getGeneration() == Generation.ND2) {
			
			data.add((byte) (enableTempProtect ? 0x01 : 0x00));
		}
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		
		firstLevelOverVolt = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		secondLevelOverVolt = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		overCurr = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		chnTempUpper = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnTempLower = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		if(Data.getGeneration() == Generation.ND2) {
			
			enableTempProtect = data.get(index++) == 0x01;
			
		}
		
	}
	
	@Override
	public Code getCode() {
		
		return MainCode.ChnFirstLevelProtectCode;
	}
	

	public double getFirstLevelOverVolt() {
		return firstLevelOverVolt;
	}

	public void setFirstLevelOverVolt(double firstLevelOverVolt) {
		this.firstLevelOverVolt = firstLevelOverVolt;
	}

	public double getSecondLevelOverVolt() {
		return secondLevelOverVolt;
	}

	public void setSecondLevelOverVolt(double secondLevelOverVolt) {
		this.secondLevelOverVolt = secondLevelOverVolt;
	}

	public double getOverCurr() {
		return overCurr;
	}

	public void setOverCurr(double overCurr) {
		this.overCurr = overCurr;
	}

	public double getChnTempUpper() {
		return chnTempUpper;
	}
	public void setChnTempUpper(double chnTempUpper) {
		this.chnTempUpper = chnTempUpper;
	}
	public double getChnTempLower() {
		return chnTempLower;
	}
	public void setChnTempLower(double chnTempLower) {
		this.chnTempLower = chnTempLower;
	}


	@Override
	public boolean supportChannel() {
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	@Override
	public String toString() {
		return "ChnFirstLevelProtectData [firstLevelOverVolt=" + firstLevelOverVolt + ", secondLevelOverVolt="
				+ secondLevelOverVolt + ", overCurr=" + overCurr + ", chnTempUpper=" + chnTempUpper + ", chnTempLower="
				+ chnTempLower + "]";
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChnFirstLevelProtectData)) {
			return false;
		}
		ChnFirstLevelProtectData other = (ChnFirstLevelProtectData) obj;
		if (Double.doubleToLongBits(chnTempLower) != Double.doubleToLongBits(other.chnTempLower)) {
			return false;
		}
		if (Double.doubleToLongBits(chnTempUpper) != Double.doubleToLongBits(other.chnTempUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(firstLevelOverVolt) != Double.doubleToLongBits(other.firstLevelOverVolt)) {
			return false;
		}
		if (Double.doubleToLongBits(overCurr) != Double.doubleToLongBits(other.overCurr)) {
			return false;
		}
		if (Double.doubleToLongBits(secondLevelOverVolt) != Double.doubleToLongBits(other.secondLevelOverVolt)) {
			return false;
		}
		
		
		return true;
	}
	public boolean isEnableTempProtect() {
		return enableTempProtect;
	}
	public void setEnableTempProtect(boolean enableTempProtect) {
		this.enableTempProtect = enableTempProtect;
	}
	
	
	
}
