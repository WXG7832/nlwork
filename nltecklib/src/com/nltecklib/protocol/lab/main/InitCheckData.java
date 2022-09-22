package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class InitCheckData extends Data implements Queryable,Responsable {

	private int chnCount; //主控附带的通道个数
	private int suppProgCount; //支持量程个数
		
	@Override
	public void encode() {
		
		data.add((byte)chnCount);
		data.add((byte)suppProgCount);
		
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
        chnCount = ProtocolUtil.getUnsignedByte(data.get(index++));
        suppProgCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
	}
	
	@Override
	public Code getCode() {
		return MainCode.InitCheckCode;
	}

	public int getSuppProgCount() {
		return suppProgCount;
	}

	public void setSuppProgCount(int suppProgCount) {
		this.suppProgCount = suppProgCount;
	}

	public int getChnCount() {
		return chnCount;
	}

	public void setChnCount(int chnCount) {
		this.chnCount = chnCount;
	}

	@Override
	public String toString() {
		return "InitCheckData [suppProgCount=" + suppProgCount + "]";
	}

	@Override
	public boolean supportChannel() {
		return false;
	}
	
	
}
