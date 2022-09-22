package com.nltecklib.protocol.lab.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 实测温度,欧姆容温控表温度
 * @author Administrator
 *
 */
public class TempQueryData extends Data implements Queryable,Responsable {

	private double temperature; //温度
	
	//超温报警标志
	private OverTempFlag overTempFlag = OverTempFlag.NORMAL;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(temperature * 10), 2, true)));
		data.add((byte) overTempFlag.ordinal());	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		temperature = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > OverTempFlag.values().length - 1) {
			 
			 throw new RuntimeException("error over temp flag : " + code);
		}
		overTempFlag = OverTempFlag.values()[code];	
	}
	
	@Override
	public Code getCode() {

		return AccessoryCode.TempQueryCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}
	

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getTemperature() {
		return temperature;
	}
	public OverTempFlag getOverTempFlag() {
		return overTempFlag;
	}

	public void setOverTempFlag(OverTempFlag overTempFlag) {
		this.overTempFlag = overTempFlag;
	}
	@Override
	public String toString() {
		return "TempQueryData [temperature=" + temperature + ", overTempFlag=" + overTempFlag + "]";
	}
	
	
	
	
}
