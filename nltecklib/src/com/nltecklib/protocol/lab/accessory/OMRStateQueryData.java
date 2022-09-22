package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * Å·Ä·ÈÝÎÂ¿Ø±í×´Ì¬
 * @author Administrator
 *
 */
public class OMRStateQueryData extends Data implements Queryable,Responsable {

	private final static int   OMR_BYTE_SIZE = 1;
	
	//ÎÂ¿Ø±íÊýÁ¿
	private int omrCount;
	private List<Byte>  omrSwitchFlag =  new ArrayList<Byte>(OMR_BYTE_SIZE);  //ÔËÐÐ×´Ì¬
	private List<Byte>  omrStateFlag =  new ArrayList<Byte>(OMR_BYTE_SIZE);  //¹ÊÕÏ×´Ì¬
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void encode() {

		
		data.add((byte) omrCount);
		data.addAll(omrSwitchFlag.subList(0, OMR_BYTE_SIZE));
		data.addAll(omrStateFlag.subList(0, OMR_BYTE_SIZE));	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		omrCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		omrSwitchFlag.clear();
		omrSwitchFlag.addAll(new ArrayList<Byte>(data.subList(index, index + OMR_BYTE_SIZE)));
		index += OMR_BYTE_SIZE;
		omrStateFlag.clear();
		omrStateFlag.addAll(new ArrayList<Byte>(data.subList(index, index + OMR_BYTE_SIZE)));
		index += OMR_BYTE_SIZE;	
	}
	
	@Override
	public Code getCode() {
		
		return AccessoryCode.OMRMeterStateCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getOmrCount() {
		return omrCount;
	}

	public void setOmrCount(int omrCount) {
		this.omrCount = omrCount;
	}

	public List<Byte> getOmrSwitchFlag() {
		return omrSwitchFlag;
	}

	public void setOmrSwitchFlag(List<Byte> omrSwitchFlag) {
		this.omrSwitchFlag = omrSwitchFlag;
	}

	public List<Byte> getOmrStateFlag() {
		return omrStateFlag;
	}

	public void setOmrStateFlag(List<Byte> omrStateFlag) {
		this.omrStateFlag = omrStateFlag;
	}

	@Override
	public String toString() {
		return "OMRStateQueryData [omrCount=" + omrCount + ", omrSwitchFlag=" + omrSwitchFlag + ", omrStateFlag="
				+ omrStateFlag + "]";
	}
	
	
}
