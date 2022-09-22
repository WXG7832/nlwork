package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BaseInfoConfigData extends Data implements Configable,Responsable{

	private int calCount;
	private byte calState;
	private int calChnCount;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public void encode() {
		data.add((byte)calCount);
		data.add(calState);
		data.add((byte)calChnCount);
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data=encodeData;
		int index=0;
		calCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		calState=(byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		calChnCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.BaseInfoConfigCode;
	}


	public int getCalCount() {
		return calCount;
	}

	public void setCalCount(int calCount) {
		this.calCount = calCount;
	}

	public byte getCalState() {
		return calState;
	}

	public void setCalState(byte calState) {
		this.calState = calState;
	}

	public int getCalChnCount() {
		return calChnCount;
	}

	public void setCalChnCount(int calChnCount) {
		this.calChnCount = calChnCount;
	}

	@Override
	public String toString() {
		return "BaseInfoConfigData [calCount=" + calCount + ", calState=" + calState + ", calChnCount=" + calChnCount
				+ "]";
	}
	
	

}
