package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;
	

/**
 * 设备电压保护
 * @author Administrator
 *
 */
public class LogicDeviceProtectData extends Data implements Configable,Queryable,Responsable{
    
	private double batteryProtectVoltage;
	private double deviceProtectVoltage; 
	private double deviceProtectCurrent;
	
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(batteryProtectVoltage * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(deviceProtectVoltage * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(deviceProtectCurrent * 10), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex =  ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		batteryProtectVoltage =  (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10 ;
	    index += 2;
	    deviceProtectVoltage =  (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10 ;
	    index += 2;
	    deviceProtectCurrent =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;

	}

	@Override
	public Code getCode() {
		
		return LogicCode.DeviceProtectCode;
	}

	public double getBatteryProtectVoltage() {
		return batteryProtectVoltage;
	}

	public void setBatteryProtectVoltage(double batteryProtectVoltage) {
		this.batteryProtectVoltage = batteryProtectVoltage;
	}

	public double getDeviceProtectVoltage() {
		return deviceProtectVoltage;
	}

	public void setDeviceProtectVoltage(double deviceProtectVoltage) {
		this.deviceProtectVoltage = deviceProtectVoltage;
	}

	public double getDeviceProtectCurrent() {
		return deviceProtectCurrent;
	}

	public void setDeviceProtectCurrent(double deviceProtectCurrent) {
		this.deviceProtectCurrent = deviceProtectCurrent;
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
