package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
* @author  wavy_zheng
* @version 创建时间：2020年3月5日 上午10:31:34
* 类说明
*/
public class DoorData extends Data implements Queryable, Responsable {
    
	
	private ValveState  state = ValveState.CLOSE;
	

	public void setDoorIndex(int doorIndex) {
		driverIndex=doorIndex;
	}
	
	public int getDoorIndex() {
		return driverIndex;
	}
	
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
		data.add((byte) state.ordinal());
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data  = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ValveState.values().length - 1) {
			
			throw new RuntimeException("error door valve state code :" + code);
		}
		state = ValveState.values()[code];
		
		
	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.DoorCode;
	}

	public ValveState getState() {
		return state;
	}

	public void setState(ValveState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "DoorData [state=" + state + ", driverIndex=" + driverIndex + "]";
	}

	
    
	

	
	
	

}
