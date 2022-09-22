package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月26日 上午10:20:36
* 类说明
*/
public class RelayControlExData extends Data implements Configable, Queryable, Responsable {
    
	private byte  relayIndex;
	private boolean connected; //继电器断开？
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
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
		
		data.add((byte) driverIndex);
		data.add(relayIndex);
		data.add((byte) (connected ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = data.get(index++);
		relayIndex = data.get(index++);
		connected = data.get(index++) == 0x01 ;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.RelayEx;
	}

	public byte getRelayIndex() {
		return relayIndex;
	}

	public void setRelayIndex(byte relayIndex) {
		this.relayIndex = relayIndex;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public String toString() {
		return "RelayControlExData [relayIndex=" + relayIndex + ", connected=" + connected + "]";
	}
	
	
	

}
