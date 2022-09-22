package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnOpt;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年7月16日 上午9:16:33
* 类说明
*/
public class ExChnsOperateData extends Data implements Configable, Responsable {
   
	private List<Short> chnIndexs = new ArrayList<>();
	private ChnOpt optType = ChnOpt.STOP;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		// 操作类型
		data.add((byte) optType.ordinal());
		//通道数
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnIndexs.size(), 2, true)));
		//通道内容
		for(Short chnIndex : chnIndexs) {
			
		    data.addAll(Arrays.asList(ProtocolUtil.split((long)chnIndex, 2, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//操作类型
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ChnOpt.values().length - 1) {

			throw new RuntimeException("error chn opt type code :" + code);
		}
		optType = ChnOpt.values()[code];
		//字节数
		int chnCount  = (int) ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//内容
		for(int n = 0 ; n < chnCount ; n++) {
			
			short chnIndex  = (short)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
			index += 2;
			chnIndexs.add(chnIndex);
		}
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ExChnOperateCode;
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


	public List<Short> getChnIndexs() {
		return chnIndexs;
	}


	public void setChnIndexs(List<Short> chnIndexs) {
		this.chnIndexs = chnIndexs;
	}


	public ChnOpt getOptType() {
		return optType;
	}


	public void setOptType(ChnOpt optType) {
		this.optType = optType;
	}
	
	

}
