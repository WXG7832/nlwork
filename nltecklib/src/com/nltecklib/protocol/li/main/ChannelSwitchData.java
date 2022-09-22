package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ChannelSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private boolean close;
	
	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte)unitIndex); 
		data.add((byte)chnIndex);
		data.add((byte) (close ? 0 : 1));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
         data = encodeData;
         int index = 0;
         unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
         chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
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
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
