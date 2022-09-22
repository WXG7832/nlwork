package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ValveSwitchData extends Data implements Configable, Queryable, Responsable{
   
	private ValveState vs = ValveState.OPEN; 
	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
        data.add((byte) vs.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ValveState.values().length - 1) {
			
			throw new RuntimeException("error valve switch code : " + code);
		}
		vs = ValveState.values()[code];

	}

	@Override
	public Code getCode() {
	
		return AccessoryCode.ValveSwitchCode;
	}

	public ValveState getVs() {
		return vs;
	}

	public void setVs(ValveState vs) {
		this.vs = vs;
	}
	
	

}
