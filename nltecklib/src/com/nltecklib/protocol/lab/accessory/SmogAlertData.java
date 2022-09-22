package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ÑÌÎí±¨¾¯Æ÷
 * @author Administrator
 *
 */
public class SmogAlertData extends Data implements Queryable,Responsable {

	private boolean alert; //ÑÌÎíÓÐ/ÎÞ±¨¾¯
	
	@Override
	public void encode() {
		
		data.add((byte) (alert ? 0x01 : 0x00));	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		
		alert = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;	
	}
	
	@Override
	public Code getCode() {
		
		return AccessoryCode.SmogAlertCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}
	
	@Override
	public boolean supportMain() {
		return false;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	@Override
	public String toString() {
		return "SmogData [alert=" + alert + "]";
	}
	
	
	
}
