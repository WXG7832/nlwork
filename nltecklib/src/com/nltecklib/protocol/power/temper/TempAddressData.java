package com.nltecklib.protocol.power.temper;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月14日 上午10:48:21
* 温度采集板通信地址烧写协议
* 该协议只能单板串口烧写，不能通过其他方式写入
*/
public class TempAddressData extends Data implements Configable, Responsable {
    
	private byte  address;
	
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
		return TemperCode.AddressCode;
	}

	@Override
	public String toString() {
		return "TempAddressData [address=" + address + "]";
	}

	public byte getAddress() {
		return address;
	}

	public void setAddress(byte address) {
		this.address = address;
	}
	
	

}
