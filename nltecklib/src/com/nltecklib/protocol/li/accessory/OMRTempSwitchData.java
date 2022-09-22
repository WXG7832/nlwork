package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 欧姆容温度开关控制协议
 * @author Administrator
 *
 */
public class OMRTempSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private boolean working;
	
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
		data.add((byte) (working ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		working = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.OMRMeterSwitchCode;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}
	
	

}
