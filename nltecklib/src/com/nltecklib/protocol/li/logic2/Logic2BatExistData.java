package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.BatExist;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月24日 下午1:35:23
* 类说明
*/
public class Logic2BatExistData extends Data implements Configable, Responsable {
   
	//启用开关
	private BatExist  batExist = BatExist.HAVE;
	private List<Byte> chnIndexList = new ArrayList<>(); //通道序号集合 
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

        data.add((byte) unitIndex);
        data.add((byte) batExist.ordinal());
        data.add((byte)chnIndexList.size());
        data.addAll(chnIndexList);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(code > BatExist.values().length - 1) {
			
			throw new RuntimeException("error bat exist  code : " + code);
		}
		batExist = BatExist.values()[code];
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndexList = data.subList(index, index + count);
		index += count;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.BatExistCode;
	}


	public BatExist getBatExist() {
		return batExist;
	}

	public void setBatExist(BatExist batExist) {
		this.batExist = batExist;
	}

	public List<Byte> getChnIndexList() {
		return chnIndexList;
	}

	public void setChnIndexList(List<Byte> chnIndexList) {
		this.chnIndexList = chnIndexList;
	}

	
	

}
