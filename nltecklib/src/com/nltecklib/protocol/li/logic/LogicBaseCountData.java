package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class LogicBaseCountData extends Data implements Queryable,Responsable{
    
	private int  version;    //逻辑板版本号
	private int  unitChnCount;   
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public void encode() {
		
        data.add((byte) unitIndex);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)unitChnCount, 2, true)));
        data.add((byte) version);
        
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		//分区通道数
		unitChnCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		//软件版本号
		version = ProtocolUtil.getUnsignedByte(data.get(index++));
		

	}

	@Override
	public Code getCode() {
		
		return LogicCode.BaseCountCode;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getUnitChnCount() {
		return unitChnCount;
	}

	public void setUnitChnCount(int unitChnCount) {
		this.unitChnCount = unitChnCount;
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
