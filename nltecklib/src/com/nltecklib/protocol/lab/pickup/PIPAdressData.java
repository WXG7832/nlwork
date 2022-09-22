package com.nltecklib.protocol.lab.pickup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月23日 上午11:56:25
* 类说明
*/
public class PIPAdressData extends Data implements Configable, Queryable, Responsable {
    
	private String ip;
	private int    port;
	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(ProtocolUtil.encodeIp(ip));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)port, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		ip = ProtocolUtil.decodeIp(data.subList(index, index+4));
		index += 4;
		port = (int) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.IPAddressCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "PIPAdressData [ip=" + ip + ", port=" + port + "]";
	}
	
	
	

}
