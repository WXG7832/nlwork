package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 通道模片继电器
 * 改写;
 * 用于大电流多膜片之间的万用表读表切换
 * @author wavy_zheng
 * 2022年8月26日
 *
 */
public class RelayControlData extends Data implements Queryable, Configable, Responsable {
	
	private boolean  connected; //已连接万用表
	
	
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
		
        data.add((byte)driverIndex);
        data.add((byte)(connected ? 1 : 0));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		connected  = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.RELAY;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	
	
	
}
