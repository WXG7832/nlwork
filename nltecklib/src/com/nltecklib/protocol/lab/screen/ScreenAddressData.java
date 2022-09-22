package com.nltecklib.protocol.lab.screen;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午7:00:43
* 液晶屏地址写入；建议使用小工具写入
*/
public class ScreenAddressData extends Data implements Configable, Queryable, Responsable {
    
	private  byte  address;
	
	@Override
	public boolean supportMain() {
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
		
		data.add(address);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		address = data.get(index++);

	}
	
	
	

	public byte getAddress() {
		return address;
	}

	public void setAddress(byte address) {
		this.address = address;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.AddressCode;
	}

	@Override
	public String toString() {
		return "ScreenAddressData [address=" + address + "]";
	}
	
	

}
