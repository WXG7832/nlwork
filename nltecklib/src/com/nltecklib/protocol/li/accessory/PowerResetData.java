package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月19日 上午11:42:19
* 类说明
*/
public class PowerResetData extends Data implements Configable, Responsable {
   
	private PowerState  ps  = PowerState.OFF;
	
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
		data.add((byte) ps.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int  index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerState.values().length - 1) {
			
			throw new RuntimeException("error power state code :" + code);
		}
		ps = PowerState.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.PowerResetCode;
	}

	public PowerState getPs() {
		return ps;
	}

	public void setPs(PowerState ps) {
		this.ps = ps;
	}

	@Override
	public String toString() {
		return "PowerResetData [ps=" + ps + "]";
	}
	
	
	

}
