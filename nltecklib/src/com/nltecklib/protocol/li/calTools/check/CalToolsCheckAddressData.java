package com.nltecklib.protocol.li.calTools.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsCheckAddressData extends Data implements Configable,Queryable, Responsable{

	
	private int address;
	
	
	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) address);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		address = ProtocolUtil.getUnsignedByte(data.get(index++));
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsCheckCode.Address;
	}

	@Override
	public String toString() {
		return "CalToolsLogicDriverAddressData [address=" + address + "]";
	}

	
}
