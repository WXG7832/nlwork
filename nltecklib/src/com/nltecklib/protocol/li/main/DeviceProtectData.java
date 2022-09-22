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

/**
 * 设备基本保护
 * @author Administrator
 *
 */
public class DeviceProtectData extends Data implements Configable,Queryable,Responsable,Cloneable{
   
	private double  batVoltUpper = 4800; //关闭通道
	private double  deviceVoltUpper =5000; //直接切断电源
	private double  currUpper = 500; //过流保护
	private double  capacityCoefficien = 2.5;
	
	@Override
	public void encode() {
		
		data.add((byte)unitIndex);		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(batVoltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(deviceVoltUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 10), 3,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(capacityCoefficien * 100), 2,true)));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex  =  ProtocolUtil.getUnsignedByte(data.get(index++));
		batVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		deviceVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currUpper = (double) ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		capacityCoefficien = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 100;
		index += 2;

	}
	
	

	

	public double getBatVoltUpper() {
		return batVoltUpper;
	}

	public void setBatVoltUpper(double batVoltUpper) {
		this.batVoltUpper = batVoltUpper;
	}

	public double getDeviceVoltUpper() {
		return deviceVoltUpper;
	}

	public void setDeviceVoltUpper(double deviceVoltUpper) {
		this.deviceVoltUpper = deviceVoltUpper;
	}

	public double getCurrUpper() {
		return currUpper;
	}

	public void setCurrUpper(double currUpper) {
		this.currUpper = currUpper;
	}

	public double getCapacityCoefficien() {
		return capacityCoefficien;
	}

	public void setCapacityCoefficien(double capacityCoefficien) {
		this.capacityCoefficien = capacityCoefficien;
	}

	@Override
	public Code getCode() {
		
		return  MainCode.DeviceProtectCode;
	}
    
	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toString() {
		return "DeviceProtectData [batVoltUpper=" + batVoltUpper + ", deviceVoltUpper=" + deviceVoltUpper
				+ ", currUpper=" + currUpper + ", capacityCoefficien=" + capacityCoefficien + "]";
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof DeviceProtectData){
			
			DeviceProtectData dpd = (DeviceProtectData) obj;
			if (dpd.getUnitIndex() == this.unitIndex && dpd.getBatVoltUpper() == this.batVoltUpper
					&& dpd.getCurrUpper() == this.currUpper && dpd.getDeviceVoltUpper() == this.deviceVoltUpper
					&& dpd.getCapacityCoefficien() == this.capacityCoefficien) {
				
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
