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

public class SlpProtectData extends Data implements Configable,Queryable,Responsable , Cloneable{
    
	private double   voltOffset;
	private double   chargeVoltAscRange; //上个充电模式结束后转入休眠电压仍上升的最大幅度
	private double   dischargeVoltDescRange; //上个放电模式结束后转入休眠电压仍下降的最大幅度
	
	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)voltOffset * 10, 2, true)));
		if(Data.isUseSleepVoltProtect()) {
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long)chargeVoltAscRange * 10, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)dischargeVoltDescRange * 10, 2, true)));
			
		}
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
        data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		voltOffset = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		if(Data.isUseSleepVoltProtect()) {
			
			chargeVoltAscRange = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
			index += 2;
			dischargeVoltDescRange = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
			index += 2;
		}
			
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

	
	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	

	@Override
	public String toString() {
		return "SlpProtectData [voltOffset=" + voltOffset + ", chargeVoltAscRange=" + chargeVoltAscRange
				+ ", dischargeVoltDescRange=" + dischargeVoltDescRange + "]";
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
		} else if (obj instanceof SlpProtectData){
			
			SlpProtectData spd = (SlpProtectData) obj;
			if (spd.getUnitIndex() == this.unitIndex 
					&& spd.getVoltOffset() == this.voltOffset) {
				
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

	public double getChargeVoltAscRange() {
		return chargeVoltAscRange;
	}

	public void setChargeVoltAscRange(double chargeVoltAscRange) {
		this.chargeVoltAscRange = chargeVoltAscRange;
	}

	public double getDischargeVoltDescRange() {
		return dischargeVoltDescRange;
	}

	public void setDischargeVoltDescRange(double dischargeVoltDescRange) {
		this.dischargeVoltDescRange = dischargeVoltDescRange;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
