package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class OMRTempModeData extends Data implements Configable, Responsable {
   
	private HeatMode  heatMode = HeatMode.AT;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) heatMode.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > HeatMode.values().length - 1) {
			
			throw new RuntimeException("error heat mode code : " + code);
		}
		heatMode = HeatMode.values()[code];

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.OMRMeterTempCode;
	}

	public HeatMode getHeatMode() {
		return heatMode;
	}

	public void setHeatMode(HeatMode heatMode) {
		this.heatMode = heatMode;
	}
	
	

}
