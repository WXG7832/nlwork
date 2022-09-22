package com.nltecklib.protocol.lab.mbcal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月30日 上午9:27:06
* 类说明
*/
public class MbIPAddressData extends Data implements Configable, Queryable, Responsable {
    
	private String ip;
	private int    port;
	
	
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
        data.addAll(Arrays.asList(ProtocolUtil.split(port , 2,true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		ip = ProtocolUtil.decodeIp(data.subList(index, index+4));
		index += 4;
		port = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;	

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.IP_ADDRESS;
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
		return "MbIPAddressData [ip=" + ip + ", port=" + port + "]";
	}
    
	
	
}
