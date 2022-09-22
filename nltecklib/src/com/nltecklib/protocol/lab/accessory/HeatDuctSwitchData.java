package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 加热管允许开关
 * @author Administrator
 *
 */
public class HeatDuctSwitchData extends Data implements Configable,Queryable,Responsable {

	private boolean working;
	
	
	@Override
	public void encode() {
		
		data.add((byte) (working ? 0x01 : 0x00));		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		working = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
	}
	
	@Override
	public Code getCode() {
		
		return AccessoryCode.HeatDuctSwitchCode;
	}
	
	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	@Override
	public String toString() {
		return "HeatDuctSwitchData [working=" + working + "]";
	}

	
	
	
}
