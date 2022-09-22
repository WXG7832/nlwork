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

public class DCProtectData extends Data implements Configable,Queryable,Responsable , Cloneable{
   
	private double  voltDescValUpper;
	private double  voltDescValLower = 1;
	private int     voltDescUnitSeconds = 300;
	private double  voltAscVal;
	private int     voltAscCount;
	private double  voltUpper;
	private double  voltLower = 3000;
	private double  currOffsetVal;
	private double  currOffsetPercent = 10;
	private double  capacityUpper;
	private int     minuteUpper;
	private double  voltWaveValUnder2000;
	private double  voltWavePercentUnder2000;
	private double  voltWaveValAbove2000;
	private double  voltWavePercentAbove2000;
	
	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		data.add((byte)voltAscCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltAscVal * 10), 2,true)));
		//电压上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltLower * 10), 2, true)));
		//电压下降速率保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltDescValUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltDescValLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(voltDescUnitSeconds , 2, true)));
		//放电电流超差
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetVal * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currOffsetPercent * 10), 2, true)));
		//电压超差波动保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWaveValUnder2000 * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWavePercentUnder2000 * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWaveValAbove2000 * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltWavePercentAbove2000 * 10), 2, true)));
		//容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityUpper * 10), 4, true)));
		//时间保护
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper , 2, true)));
		  
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		voltAscCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		//电压上升幅度阀值
		voltAscVal = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		//电压上下限
		voltUpper = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		//电压下降速率保护
		voltDescValUpper = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltDescValLower = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltDescUnitSeconds = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		
		//电流超差保护值		
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
		
		return MainCode.DCProtectCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public void setVoltAscVal(int voltAscVal) {
		this.voltAscVal = voltAscVal;
	}

	public int getVoltAscCount() {
		return voltAscCount;
	}

	public void setVoltAscCount(int voltAscCount) {
		this.voltAscCount = voltAscCount;
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
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public String toString() {
		return "DCProtectData [voltDescValUpper=" + voltDescValUpper + ", voltDescValLower=" + voltDescValLower
				+ ", voltDescUnitSeconds=" + voltDescUnitSeconds + ", voltAscVal=" + voltAscVal + ", voltAscCount="
				+ voltAscCount + ", voltUpper=" + voltUpper + ", voltLower=" + voltLower + ", currOffsetVal="
				+ currOffsetVal + ", currOffsetPercent=" + currOffsetPercent + ", capacityUpper=" + capacityUpper
				+ ", minuteUpper=" + minuteUpper + ", voltWaveValUnder2000=" + voltWaveValUnder2000
				+ ", voltWavePercentUnder2000=" + voltWavePercentUnder2000 + ", voltWaveValAbove2000="
				+ voltWaveValAbove2000 + ", voltWavePercentAbove2000=" + voltWavePercentAbove2000 + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof DCProtectData){
			
			DCProtectData dcpd = (DCProtectData) obj;
			if (dcpd.getUnitIndex() == this.unitIndex
					&& dcpd.getVoltDescValUpper() == this.voltDescValUpper
					&& dcpd.getVoltDescValLower() == this.voltDescValLower
					&& dcpd.getVoltDescUnitSeconds() == this.voltDescUnitSeconds
					&& dcpd.getVoltAscVal() == this.voltAscVal
					&& dcpd.voltAscCount == this.voltAscCount
					&& dcpd.voltUpper == this.voltUpper
					&& dcpd.voltLower == this.voltLower
					&& dcpd.getCurrOffsetVal() == this.currOffsetVal
					&& dcpd.getCurrOffsetPercent() == this.currOffsetPercent
					&& dcpd.getCapacityUpper() == this.capacityUpper
					&& dcpd.getMinuteUpper() == this.minuteUpper
					&& dcpd.getVoltWaveValUnder2000() == this.voltWaveValUnder2000
					&& dcpd.getVoltWavePercentUnder2000() == this.voltWavePercentUnder2000
					&& dcpd.getVoltWaveValAbove2000() == this.voltWaveValAbove2000
					&& dcpd.getVoltWavePercentAbove2000() == this.voltWavePercentAbove2000) {
				
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
