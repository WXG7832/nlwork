package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月19日 上午10:08:22
* 类说明
*/
public class UnitAddressData extends Data implements Configable, Responsable {
    
	private String ip;
	
	
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
		
		data.addAll(ProtocolUtil.encodeIp(ip));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index  = 0;
		
		ip = ProtocolUtil.decodeIp(data.subList(index, index+4));
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitAddressCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "UnitAddressData [ip=" + ip + ", mainIndex=" + mainIndex + "]";
	}
	
	

}
