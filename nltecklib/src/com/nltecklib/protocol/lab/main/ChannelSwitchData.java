package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;
@Deprecated
public class ChannelSwitchData extends Data implements Configable,Queryable,Responsable {

	private boolean close;
	
	@Override
	public void encode() {
		
		//主控地址和通道号
		data.add((byte)mainIndex);
		data.add((byte)chnIndex);
		data.add((byte) (close ? 0 : 1));
		
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
		mainIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		close = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
	}
	
	@Override
	public Code getCode() {
		
		return MainCode.ChnSwitchCode;
	}
	

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	@Override
	public String toString() {
		return "ChannelSwitchData [close=" + close + "]";
	}

	@Override
	public boolean supportChannel() {
		return false;
	}
	

	

	
}
