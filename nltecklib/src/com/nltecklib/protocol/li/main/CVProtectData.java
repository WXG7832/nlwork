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

public class CVProtectData extends Data implements Configable,Queryable,Responsable , Cloneable{
    
	private double  currAscVal;
	private int     currAscCount;
	private double  currUpper = 3000;
	private double  currLower;
	
	private double  voltOffsetVal;
	private double  voltOffsetPercent = 10;
	private double  capacityUpper;
	private int     minuteUpper;
	
	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		data.add((byte)currAscCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currAscVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currLower * 10), 3, true)));

		//电压超差波动保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltOffsetVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltOffsetPercent * 10), 2,true)));
		//容量保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper * 10), 4,true)));
		//时间保护
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper , 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		currAscCount =  ProtocolUtil.getUnsignedByte(data.get(index++));
		currAscVal   =  (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currUpper = (double) ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		currLower = (double) ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		
		//电压超差保护
		voltOffsetVal = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltOffsetPercent = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		//容量保护
		capacityUpper = (double) ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true) / 10;
		index += 4;
		//时间保护
		minuteUpper = (int) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) ;
		index += 2;
	}

	@Override
	public Code getCode() {
		
		return MainCode.CVProtectCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	

	public double getCurrAscVal() {
		return currAscVal;
	}

	public void setCurrAscVal(double currAscVal) {
		this.currAscVal = currAscVal;
	}

	public int getCurrAscCount() {
		return currAscCount;
	}

	public void setCurrAscCount(int currAscCount) {
		this.currAscCount = currAscCount;
	}

	public double getCurrUpper() {
		return currUpper;
	}

	public void setCurrUpper(double currUpper) {
		this.currUpper = currUpper;
	}

	public double getCurrLower() {
		return currLower;
	}

	public void setCurrLower(double currLower) {
		this.currLower = currLower;
	}


	public double getVoltOffsetVal() {
		return voltOffsetVal;
	}

	public void setVoltOffsetVal(double voltOffsetVal) {
		this.voltOffsetVal = voltOffsetVal;
	}

	public double getVoltOffsetPercent() {
		return voltOffsetPercent;
	}

	public void setVoltOffsetPercent(double voltOffsetPercent) {
		this.voltOffsetPercent = voltOffsetPercent;
	}

	public double getCapacityUpper() {
		return capacityUpper;
	}

	public void setCapacityUpper(double capacityUpper) {
		this.capacityUpper = capacityUpper;
	}

	public int getMinuteUpper() {
		return minuteUpper;
	}

	public void setMinuteUpper(int minuteUpper) {
		this.minuteUpper = minuteUpper;
	}
	

	@Override
	public String toString() {
		return "CVProtectData [currAscVal=" + currAscVal + ", currAscCount=" + currAscCount + ", currUpper=" + currUpper
				+ ", currLower=" + currLower + ", voltOffsetVal=" + voltOffsetVal + ", voltOffsetPercent="
				+ voltOffsetPercent + ", capacityUpper=" + capacityUpper + ", minuteUpper=" + minuteUpper + "]";
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof CVProtectData){
			
			CVProtectData cvpd = (CVProtectData) obj;
			if (cvpd.getUnitIndex() == this.unitIndex
					&& cvpd.getCurrAscVal() == this.currAscVal
					&& cvpd.getCurrAscCount() == this.currAscCount
					&& cvpd.getCurrUpper() == this.currUpper
					&& cvpd.getCurrLower() == this.currLower
					&& cvpd.getVoltOffsetVal() == this.voltOffsetVal
					&& cvpd.getVoltOffsetPercent() == this.voltOffsetPercent
					&& cvpd.capacityUpper == this.capacityUpper
					&& cvpd.getMinuteUpper() == this.minuteUpper) {
				
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
