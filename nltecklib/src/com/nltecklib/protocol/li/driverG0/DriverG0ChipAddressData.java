package com.nltecklib.protocol.li.driverG0;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 *    –æ∆¨µÿ÷∑≈‰÷√£¨ø…∂¡ø…–¥
 * @author admin
 *
 */
public class DriverG0ChipAddressData extends DriverG0Data  implements Queryable,Configable, Responsable {

	
	private int address;
	
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
		
		int index = 0;
		data = encodeData;
		address = ProtocolUtil.getUnsignedByte(data.get(index));
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverG0Code.ChipAddressCode;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

}
