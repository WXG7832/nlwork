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

public class DPProtectData extends Data implements Configable,Queryable,Responsable,Cloneable {
    
	/**
	 * 电压上限mV
	 */
	private double  voltUpper;
	/**
	 * 电压下限mV
	 */
	private double  voltLower ;
	/**
	 * 电流上限mA
	 */
	private double  currUpper;
	/**
	 * 电流下限mA
	 */
	private double  currLower;
	/**
	 * 功率超差值mW
	 */
	private double  poweOffsetVal;
	/**
	 * 功率超差百分比
	 */
	private double  poweOffsetPercent;
	/**
	 * 容量上限mAh
	 */
	private double  capacityUpper;
	/**
	 * 工步时间上限min
	 */
	private int     minuteUpper;
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		//电压电流上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currLower * 10), 2,true)));
		//功率超差波动值和百分比
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(poweOffsetVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(poweOffsetPercent * 10), 2,true)));
		//容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper), 4, true)));
		//时间保护
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper , 2, true)));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		//电压电流上下限
		voltUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//功率超差波动值和百分比
		poweOffsetVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		poweOffsetPercent = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//容量
		capacityUpper = (double)ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		index += 4;
		//时间保护
		minuteUpper = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		
	}
	
	@Override
	public Code getCode() {
		return MainCode.DPProtectCode;
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

	public double getPoweOffsetVal() {
		return poweOffsetVal;
	}

	public void setPoweOffsetVal(double poweOffsetVal) {
		this.poweOffsetVal = poweOffsetVal;
	}

	public double getPoweOffsetPercent() {
		return poweOffsetPercent;
	}

	public void setPoweOffsetPercent(double poweOffsetPercent) {
		this.poweOffsetPercent = poweOffsetPercent;
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
		return "DPProtectData [voltUpper=" + voltUpper + ", voltLower=" + voltLower + ", currUpper=" + currUpper
				+ ", currLower=" + currLower + ", poweOffsetVal=" + poweOffsetVal + ", poweOffsetPercent="
				+ poweOffsetPercent + ", capacityUpper=" + capacityUpper + ", minuteUpper=" + minuteUpper + "]";
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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DPProtectData)) {
			return false;
		}
		DPProtectData other = (DPProtectData) obj;
		if (Double.doubleToLongBits(capacityUpper) != Double.doubleToLongBits(other.capacityUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(currLower) != Double.doubleToLongBits(other.currLower)) {
			return false;
		}
		if (Double.doubleToLongBits(currUpper) != Double.doubleToLongBits(other.currUpper)) {
			return false;
		}
		if (minuteUpper != other.minuteUpper) {
			return false;
		}
		if (Double.doubleToLongBits(poweOffsetPercent) != Double.doubleToLongBits(other.poweOffsetPercent)) {
			return false;
		}
		if (Double.doubleToLongBits(poweOffsetVal) != Double.doubleToLongBits(other.poweOffsetVal)) {
			return false;
		}
		if (Double.doubleToLongBits(voltLower) != Double.doubleToLongBits(other.voltLower)) {
			return false;
		}
		if (Double.doubleToLongBits(voltUpper) != Double.doubleToLongBits(other.voltUpper)) {
			return false;
		}
		return true;
	}

	
}
