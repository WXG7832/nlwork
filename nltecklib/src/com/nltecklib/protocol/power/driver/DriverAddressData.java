package com.nltecklib.protocol.power.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月13日 下午5:57:47
* 类说明
*/
public class DriverAddressData extends Data implements Configable, Queryable, Responsable {
    
	private byte  address; //驱动板地址号
	
	
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
		
		data.add(address);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		address = data.get(0);

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.DriverAddressCode;
	}

	public byte getAddress() {
		return address;
	}

	public void setAddress(byte address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "DriverAddressData [address=" + address + "]";
	}
	
	

}
