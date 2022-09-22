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

public class DCProtectData extends Data implements Configable,Queryable,Responsable,Cloneable {
	
	/**
	 * 电压连续上升个数，需要和上升幅度一起配置
	 */
	private int     voltAscCount;
	/**
	 * 电压连续上升幅度
	 */
	private double  voltAscVal;
	/**
	 * 电压上限值
	 */
	private double  voltUpper;
	/**
	 * 电压下限值
	 */
	private double  voltLower ;
	/**
	 * 电压下降斜率
	 */
	private double  voltDescValUpper;
	private double  voltDescValLower;
	private int     voltDescUnitSeconds;
	/**
	 * 电流超差绝对值
	 */
	private double  currOffsetVal;
	/**
	 * 电流超差百分比
	 */
	private double  currOffsetPercent;
	/**
	 * 电压超差绝对值mV
	 */
	private double  voltWaveVal;
	/**
	 * 电压超差百分比
	 */
	private double  voltWavePercent;
	/**
	 * 容量上限mAh
	 */
	private double  capacityUpper;
	/**
	 * 时间上限，单位min
	 */
	private int     minuteUpper;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.add((byte)voltAscCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltAscVal * 10), 2,true)));
		//电压上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2,true)));
		//电压下降速率
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltDescValUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltDescValLower * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(voltDescUnitSeconds , 2, true)));
		//电流超差波动
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetPercent * 10), 2,true)));
		//电压超差保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWaveVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWavePercent * 10), 2,true)));
		//容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper), 4, true)));
		//时间
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper , 2, true)));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		voltAscCount =  ProtocolUtil.getUnsignedByte(data.get(index++));
		voltAscVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电压上下限
		voltUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电压下降速率
		voltDescValUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltDescValLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltDescUnitSeconds = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//电流超差波动
		currOffsetVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currOffsetPercent = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电压超差保护
		voltWaveVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltWavePercent = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//容量
		capacityUpper = (double)ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		index += 4;
		//时间
		minuteUpper = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		
	}
	
	@Override
	public Code getCode() {
		return MainCode.DCProtectCode;
	}

	
	public int getVoltAscCount() {
		return voltAscCount;
	}

	public void setVoltAscCount(int voltAscCount) {
		this.voltAscCount = voltAscCount;
	}

	public double getVoltAscVal() {
		return voltAscVal;
	}

	public void setVoltAscVal(double voltAscVal) {
		this.voltAscVal = voltAscVal;
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

	public double getVoltDescValUpper() {
		return voltDescValUpper;
	}

	public void setVoltDescValUpper(double voltDescValUpper) {
		this.voltDescValUpper = voltDescValUpper;
	}

	public double getVoltDescValLower() {
		return voltDescValLower;
	}

	public void setVoltDescValLower(double voltDescValLower) {
		this.voltDescValLower = voltDescValLower;
	}

	public int getVoltDescUnitSeconds() {
		return voltDescUnitSeconds;
	}

	public void setVoltDescUnitSeconds(int voltDescUnitSeconds) {
		this.voltDescUnitSeconds = voltDescUnitSeconds;
	}

	public double getCurrOffsetVal() {
		return currOffsetVal;
	}

	public void setCurrOffsetVal(double currOffsetVal) {
		this.currOffsetVal = currOffsetVal;
	}

	public double getCurrOffsetPercent() {
		return currOffsetPercent;
	}

	public void setCurrOffsetPercent(double currOffsetPercent) {
		this.currOffsetPercent = currOffsetPercent;
	}

	public double getVoltWaveVal() {
		return voltWaveVal;
	}
	public void setVoltWaveVal(double voltWaveVal) {
		this.voltWaveVal = voltWaveVal;
	}
	public double getVoltWavePercent() {
		return voltWavePercent;
	}
	public void setVoltWavePercent(double voltWavePercent) {
		this.voltWavePercent = voltWavePercent;
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
		return "DCProtectData [voltAscCount=" + voltAscCount + ", voltAscVal=" + voltAscVal + ", voltUpper=" + voltUpper
				+ ", voltLower=" + voltLower + ", voltDescValUpper=" + voltDescValUpper + ", voltDescValLower="
				+ voltDescValLower + ", voltDescUnitSeconds=" + voltDescUnitSeconds + ", currOffsetVal=" + currOffsetVal
				+ ", currOffsetPercent=" + currOffsetPercent + ", voltWaveVal=" + voltWaveVal + ", voltWavePercent="
				+ voltWavePercent + ", capacityUpper=" + capacityUpper + ", minuteUpper=" + minuteUpper + "]";
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
		if (!(obj instanceof DCProtectData)) {
			return false;
		}
		DCProtectData other = (DCProtectData) obj;
		if (Double.doubleToLongBits(capacityUpper) != Double.doubleToLongBits(other.capacityUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(currOffsetPercent) != Double.doubleToLongBits(other.currOffsetPercent)) {
			return false;
		}
		if (Double.doubleToLongBits(currOffsetVal) != Double.doubleToLongBits(other.currOffsetVal)) {
			return false;
		}
		if (minuteUpper != other.minuteUpper) {
			return false;
		}
		if (voltAscCount != other.voltAscCount) {
			return false;
		}
		if (Double.doubleToLongBits(voltAscVal) != Double.doubleToLongBits(other.voltAscVal)) {
			return false;
		}
		if (voltDescUnitSeconds != other.voltDescUnitSeconds) {
			return false;
		}
		if (Double.doubleToLongBits(voltDescValLower) != Double.doubleToLongBits(other.voltDescValLower)) {
			return false;
		}
		if (Double.doubleToLongBits(voltDescValUpper) != Double.doubleToLongBits(other.voltDescValUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(voltLower) != Double.doubleToLongBits(other.voltLower)) {
			return false;
		}
		if (Double.doubleToLongBits(voltUpper) != Double.doubleToLongBits(other.voltUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(voltWavePercent) != Double.doubleToLongBits(other.voltWavePercent)) {
			return false;
		}
		if (Double.doubleToLongBits(voltWaveVal) != Double.doubleToLongBits(other.voltWaveVal)) {
			return false;
		}
		return true;
	}
	
	
	
	
	
}
