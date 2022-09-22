package com.nltecklib.protocol.li.test.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.PowerType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月24日 上午11:32:16
* 电源开关
*/
public class DTPowerSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private PowerType powerType;
	private boolean   isOpen;
	
	
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
		data.add((byte) powerType.ordinal());
		data.add((byte) (isOpen ? 0x01 : 0x00));
 
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerType.values().length - 1) {
			
			throw new RuntimeException("error power type code : " + code);
		}
		powerType = PowerType.values()[code];
		isOpen = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverTestCode.PowerSwitch;
	}

	public PowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(PowerType powerType) {
		this.powerType = powerType;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	

}
