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

public class CCProtectData extends Data implements Configable,Queryable,Responsable,Cloneable{
    
	private double  voltDescVal;   //在充电时电压发生连续下降的单次幅度值
	private int     voltDescCount; //在充电时电压发生连续下降的次数
	private double  voltAscValUpper; //电压上升斜率保护单位时间内上升上限值
	private double  voltAscValLower = DEFAULT_CC_ASC_VAL_LOWER; //电压上升斜率保护单位时间内上升下限值
	private int     voltAscUnitSeconds = DEFAULT_CC_ASC_UNIT_SECOND;  //上升速率保护单位检测时间s
	private double  voltUpper = DEFAULT_CC_VOLT_UPPER; //电压上限值
	private double  voltLower;  //电压下限值 
	private double  currOffsetVal;  //电流超差绝对值
	private double  currOffsetPercent = DEFAULT_CC_CURRENT_OFFSET_PERCENT; //电流超差百分比
	private double  capacityUpper; //容量上限
	private int     minuteUpper;  //时间上限
	private double  voltWaveValUnder2000;  //2000mAh以下电压波动超差绝对值
	private double  voltWavePercentUnder2000; //2000mAh以下电压波动超差百分比
	private double  voltWaveValAbove2000;  //2000mAh以上电压波动超差绝对值
	private double  voltWavePercentAbove2000; //2000mAh以上电压波动超差百分比
	
	@Override
	public void encode() {		
       
		data.add((byte)unitIndex);
		data.add((byte)voltDescCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltDescVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2,true)));	
		//电压上升速率限值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltAscValUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltAscValLower * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(voltAscUnitSeconds , 2,true)));
		
		//电流波动差值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetVal * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetPercent * 10), 2,true)));
		
		//电压波动超差限值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWaveValUnder2000 * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWavePercentUnder2000 * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWaveValAbove2000 * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWavePercentAbove2000 * 10), 2,true)));
		
		//容量保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper * 10), 4,true)));
		//时间保护
		data.addAll(Arrays.asList(ProtocolUtil.split( minuteUpper, 2 , true)));
		  
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		voltDescCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		voltDescVal = (int) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltUpper = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电压上升速率限值
		voltAscValUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltAscValLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltAscUnitSeconds = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//电流波动差值
		currOffsetVal = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currOffsetPercent = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		//电压波动超差
		voltWaveValUnder2000 = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltWavePercentUnder2000 = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltWaveValAbove2000 = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltWavePercentAbove2000 = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		//容量保护
		capacityUpper = (double)ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true) / 10;
		index += 4;
		//时间保护
		minuteUpper = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;	
		
	}

	@Override
	public Code getCode() {
		return MainCode.CCProtectCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	

	public int getVoltDescCount() {
		return voltDescCount;
	}

	public void setVoltDescCount(int voltDescCount) {
		this.voltDescCount = voltDescCount;
	}
	
	

	

	public double getVoltDescVal() {
		return voltDescVal;
	}

	public void setVoltDescVal(double voltDescVal) {
		this.voltDescVal = voltDescVal;
	}

	public double getVoltAscValUpper() {
		return voltAscValUpper;
	}

	public void setVoltAscValUpper(double voltAscValUpper) {
		this.voltAscValUpper = voltAscValUpper;
	}

	public double getVoltAscValLower() {
		return voltAscValLower;
	}

	public void setVoltAscValLower(double voltAscValLower) {
		this.voltAscValLower = voltAscValLower;
	}

	public int getVoltAscUnitSeconds() {
		return voltAscUnitSeconds;
	}

	public void setVoltAscUnitSeconds(int voltAscUnitSeconds) {
		this.voltAscUnitSeconds = voltAscUnitSeconds;
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

	public double getVoltWaveValUnder2000() {
		return voltWaveValUnder2000;
	}

	public void setVoltWaveValUnder2000(double voltWaveValUnder2000) {
		this.voltWaveValUnder2000 = voltWaveValUnder2000;
	}

	public double getVoltWavePercentUnder2000() {
		return voltWavePercentUnder2000;
	}

	public void setVoltWavePercentUnder2000(double voltWavePercentUnder2000) {
		this.voltWavePercentUnder2000 = voltWavePercentUnder2000;
	}

	public double getVoltWaveValAbove2000() {
		return voltWaveValAbove2000;
	}

	public void setVoltWaveValAbove2000(double voltWaveValAbove2000) {
		this.voltWaveValAbove2000 = voltWaveValAbove2000;
	}

	public double getVoltWavePercentAbove2000() {
		return voltWavePercentAbove2000;
	}

	public void setVoltWavePercentAbove2000(double voltWavePercentAbove2000) {
		this.voltWavePercentAbove2000 = voltWavePercentAbove2000;
	}

	

	@Override
	public String toString() {
		return "CCProtectData [voltDescVal=" + voltDescVal + ", voltDescCount=" + voltDescCount + ", voltAscValUpper="
				+ voltAscValUpper + ", voltAscValLower=" + voltAscValLower + ", voltAscUnitSeconds="
				+ voltAscUnitSeconds + ", voltUpper=" + voltUpper + ", voltLower=" + voltLower + ", currOffsetVal="
				+ currOffsetVal + ", currOffsetPercent=" + currOffsetPercent + ", capacityUpper=" + capacityUpper
				+ ", minuteUpper=" + minuteUpper + ", voltWaveValUnder2000=" + voltWaveValUnder2000
				+ ", voltWavePercentUnder2000=" + voltWavePercentUnder2000 + ", voltWaveValAbove2000="
				+ voltWaveValAbove2000 + ", voltWavePercentAbove2000=" + voltWavePercentAbove2000 + "]";
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return  true;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof CCProtectData){
			
			CCProtectData ccpd = (CCProtectData) obj;
			if (ccpd.getUnitIndex() == this.unitIndex 
					&& ccpd.getVoltDescVal() == this.voltDescVal
					&& ccpd.getVoltDescCount() == this.voltDescCount
					&& ccpd.getVoltAscValUpper() == this.voltAscValUpper
					&& ccpd.getVoltAscValLower() == this.voltAscValLower
					&& ccpd.getVoltAscUnitSeconds() == this.voltAscUnitSeconds
					&& ccpd.getVoltUpper() == this.voltUpper
					&& ccpd.getVoltLower() == this.voltLower
					&& ccpd.getCurrOffsetVal() == this.currOffsetVal
					&& ccpd.getCurrOffsetPercent() == this.currOffsetPercent
					&& ccpd.capacityUpper == this.capacityUpper
					&& ccpd.minuteUpper == this.minuteUpper
					&& ccpd.voltWaveValUnder2000 == this.voltWaveValUnder2000
					&& ccpd.getVoltWavePercentUnder2000() == this.voltWavePercentUnder2000
					&& ccpd.getVoltWaveValAbove2000() == this.voltWavePercentUnder2000
					&& ccpd.getVoltWavePercentAbove2000() == this.voltWavePercentAbove2000) {
				
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
		return super.clone();
	}

}
