package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ÑÌÎí±¨¾¯Æ÷
 * @author Administrator
 *
 */
public class SmogAlertData extends Data implements Configable, Queryable, Responsable {
    
	private AlertState alertState = AlertState.NORMAL;
	
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
		data.add((byte) alertState.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        int code     = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(code > AlertState.values().length - 1) {
        	
        	throw new RuntimeException("error alert state code : " + code);
        }
        alertState = AlertState.values()[code];
	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.SmogAlertCode;
	}

	public AlertState getAlertState() {
		return alertState;
	}

	public void setAlertState(AlertState alertState) {
		this.alertState = alertState;
	}

	@Override
	public String toString() {
		return "SmogAlertData [alertState=" + alertState + ", driverIndex=" + driverIndex + "]";
	}

	
	
	

}
