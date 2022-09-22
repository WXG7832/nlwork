package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @Description: 控制板的IP配置
 * @Author: JinMei
 * @Date  : 2020/03/26 10:39:41 
 */
public class CIPAddressData extends Data implements Configable,Responsable {
	
	private final static int IP_SIZE = 4;
	
	private int[] ip = new int[IP_SIZE];	//ip
	private int port = 0; //0不修改
	
	
	@Override
	public void encode() {
		
		for (int i = 0; i < ip.length; i++) {
			data.add((byte)ip[i]);
		}
		data.addAll(Arrays.asList(ProtocolUtil.split(port , 2,true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;		
		int index = 0;	

		for (int i = 0; i < IP_SIZE; i++) {
			
			ip[i] = ProtocolUtil.getUnsignedByte(data.get(index++));
		}
		port = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;	
	}

	@Override
	public Code getCode() {
		return CalCode.IP_ADDRESS;
	}
	
	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}
	
	public String getIpAddress() {

		StringBuffer b = new StringBuffer();
		for (int i = 0; i < ip.length; i++) {
			
			b.append(ip[i]);
			if(i < ip.length - 1) {
				b.append(".");
			}
		}
		return b.toString();
	}
	
	public void setIpAddress(String ip) {
		
		String[] secs = ip.split("\\.");
		for(int n = 0 ; n < secs.length ; n++) {
			
			int val = Integer.parseInt(secs[n]);
			this.ip[n] = val;
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < ip.length; i++) {
			
			b.append(ip[i]);
			if(i < ip.length - 1) {
				b.append(".");
			}
		}
		return b.toString() + " " + port;
	}

}
