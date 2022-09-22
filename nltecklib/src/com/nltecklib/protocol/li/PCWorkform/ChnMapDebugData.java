package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ChnMapDebugData extends Data implements Configable, Responsable,Queryable{
	private boolean enable;
	private List<Integer>chnMatchList=new ArrayList<>();
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
		data.add((byte) (enable ? 0x01 : 0x00));
		data.add((byte) driverChnCount);
		for(int chnIndex:chnMatchList) {
			data.add((byte) chnIndex);
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		chnMatchList.clear();
		data = encodeData;
		int index = 0;
		System.out.println("encodeData:"+encodeData);
		enable=ProtocolUtil.getUnsignedByte(data.get(index++))==0x01?true:false;
		Data.setDriverChnCount(ProtocolUtil.getUnsignedByte(data.get(index++)));
		for(int i=0;i<driverChnCount;i++) {
			chnMatchList.add(ProtocolUtil.getUnsignedByte(data.get(index++)));
		}
	}

	@Override
	public Code getCode() {
		return PCWorkformCode.ChnMapDebugCode;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setChnMatchList(List<Integer> chnMatchList) {
		this.chnMatchList = chnMatchList;
	}

	public List<Integer> getChnMatchList() {
		return chnMatchList;
	}
}	
