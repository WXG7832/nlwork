package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 电源控制协议
 * @author Administrator
 *
 */
public class PowerSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private PowerType   powerType = PowerType.CHARGE;
	private PowerState     state = PowerState.OFF;
	
	@Override
	public boolean supportUnit() {
	
		return true;
	}

	@Override
	public boolean supportDriver() {
		
		return true;  //电源类型
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) powerType.ordinal());
		data.add((byte) state.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerType.values().length - 1) {
			
			throw new RuntimeException("error power type code :" + code);
		}
		driverIndex = code;
		powerType = PowerType.values()[code];
		
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerState.values().length - 1) {
			
			throw new RuntimeException("error power state code : " + code);
		}
		//开关状态
		state = PowerState.values()[code];
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.PowerSwitchCode;
	}

	public PowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(PowerType powerType) {
		this.powerType = powerType;
		this.driverIndex = powerType.ordinal();
	}

	public PowerState getState() {
		return state;
	}

	public void setState(PowerState state) {
		this.state = state;
	}
	
	

}
