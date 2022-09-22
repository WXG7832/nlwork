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

public class CVProtectData extends Data implements Configable,Queryable,Responsable,Cloneable {
    
	/**
	 * 电流连续上升个数，必须和连续上升幅度一起配置
	 */
	private int     currAscCount;
	/**
	 * 电流连续上升幅度mV
	 */
	private double  currAscVal;	
	/**
	 * 电流上限mA
	 */
	private double  currUpper ;
	/**
	 * 电流下限mA
	 */
	private double  currLower;
	/**
	 * 电压超差值,单位mV
	 */
	private double  voltOffsetVal;
	/**
	 * 电压超差百分比%
	 */
	private double  voltOffsetPercent;
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
		
		data.add((byte)currAscCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currAscVal * 10), 2,true)));
		//电流上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currLower * 10), 3, true)));
		//电压超差波动
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltOffsetVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltOffsetPercent * 10), 2,true)));
		//容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper), 4,true)));
		//时间
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper , 2,true)));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		currAscCount =  ProtocolUtil.getUnsignedByte(data.get(index++));
		currAscVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电流上下限
		currUpper = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		currLower = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		//电压超差波动
		voltOffsetVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltOffsetPercent = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//容量
		capacityUpper = (double) ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		index += 4;
		//时间
		minuteUpper = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		
	}
	
	@Override
	public Code getCode() {
		return MainCode.CVProtectCode;
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
		if (!(obj instanceof CVProtectData)) {
			return false;
		}
		CVProtectData other = (CVProtectData) obj;
		if (Double.doubleToLongBits(capacityUpper) != Double.doubleToLongBits(other.capacityUpper)) {
			return false;
		}
		if (currAscCount != other.currAscCount) {
			return false;
		}
		if (Double.doubleToLongBits(currAscVal) != Double.doubleToLongBits(other.currAscVal)) {
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
		if (Double.doubleToLongBits(voltOffsetPercent) != Double.doubleToLongBits(other.voltOffsetPercent)) {
			return false;
		}
		if (Double.doubleToLongBits(voltOffsetVal) != Double.doubleToLongBits(other.voltOffsetVal)) {
			return false;
		}
		return true;
	}
	
	
}
