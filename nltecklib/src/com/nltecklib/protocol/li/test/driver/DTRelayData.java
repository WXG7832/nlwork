package com.nltecklib.protocol.li.test.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.BurnRelayType;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月25日 上午8:58:01
* 类说明
*/
public class DTRelayData extends Data implements Configable, Queryable, Responsable {
   
	private  BurnRelayType relayType;
	private  boolean    connected;
	
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
		data.add((byte) relayType.ordinal());
		data.add((byte) (connected ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > BurnRelayType.values().length - 1) {
			
			throw new RuntimeException("error burn type code :" + code);
		}
		relayType = BurnRelayType.values()[code];
		connected = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverTestCode.RelayCode;
	}

	public BurnRelayType getRelayType() {
		return relayType;
	}

	public void setRelayType(BurnRelayType relayType) {
		this.relayType = relayType;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	

}
