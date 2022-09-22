package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class IPAddressData extends Data implements Configable,Responsable {

	private int  addr1 = 192;
	private int  addr2 = 168;
	private int  addr3 = 1;
	private int  addr4 = 127;
	
	
	@Override
	public void encode() {
		
		data.add((byte)addr1);
		data.add((byte)addr2);
		data.add((byte)addr3);
		data.add((byte)addr4);
		
		
	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		addr1 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		addr2 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		addr3 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		addr4 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		
	}
	
	@Override
	public Code getCode() {

		return MainCode.IPAddressCode;
	}
	
	
	@Override
	public boolean supportChannel() {
		return false;
	}
	
	
	public String getIpAddress() {
		
		return addr1 + "." + addr2 + "." + addr3 + "." + addr4;
	}
	
	public void SetIpAddress(String ip) {
		
		String[] secs = ip.split("\\.");
		for(int n = 0 ; n < secs.length ; n++) {
			
			int val = Integer.parseInt(secs[n]);
			switch(n) {
			
			case 0:
				addr1 = val;
				break;
			case 1:
				addr2 = val;
				break;
			case 2:
				addr3 = val;
				break;
			case 3:
				addr4 = val;
				break;
			default:
				break;
				
			}
		}
	}
	@Override
	public String toString() {
		return addr1 + "." + addr2 + "." + addr3 + "." + addr4;
	}

	
	
}
