package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.util.ProtocolUtil;


public class TurboFanData extends Data implements Configable, Responsable, Queryable {
   
	private PowerState  powerState = PowerState.OFF;
	
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
        data.add((byte) powerState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerState.values().length - 1) {
			
			throw new RuntimeException("error power state code : " + code);
		}
		powerState = PowerState.values()[code];

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.TurboFanCode;
	}

	public PowerState getPowerState() {
		return powerState;
	}

	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}
	
	

}
