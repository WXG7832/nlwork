package com.nltecklib.protocol.lab.screen;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午4:55:28
* 类说明
*/
public class IpAddressData extends Data implements Configable, Queryable, Responsable {
   
	private byte[]  ipAddress = new byte[4];
	
	
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
		
		for(int n = 0 ; n < ipAddress.length ; n++) {
		     data.add(ipAddress[n]);
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		for(int n = 0 ; n < ipAddress.length ; n++ ) {
			
			ipAddress[n] = data.get(index++);
			
		}

	}
	
	public void setIpAddress(String ip) {
		
		String[] secs = ip.split("\\.");
		for(int n = 0 ; n < ipAddress.length ; n++) {
			
			ipAddress[n] = (byte) Integer.parseInt(secs[n]);
		}
		
	}
	
	
	public String getIpAddress() {
		
		StringBuffer info = new StringBuffer();
         for(int n = 0 ; n < ipAddress.length ; n++) {
			
			 info.append(ProtocolUtil.getUnsignedByte(ipAddress[n]));
			 if(n < ipAddress.length - 1) {
				 
				 info.append(".");
			 }
		}
         
         return info.toString();
		
	}
	
	

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.IpAddressCode;
	}
	
	

}
