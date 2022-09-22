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
 * 纽联旧温控板通信协议，已弃用
 * @author Administrator
 *
 */
public class NLTempData extends Data implements Configable, Queryable, Responsable {
    
	private boolean open;
	private int     temperature;
	
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
	      data.add((byte) (open ? 0x01 : 0x00));
	      data.add((byte) temperature);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
        data = encodeData;
        driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        //open？
        open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
        //temperature
        temperature = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.NLTempCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	

}
