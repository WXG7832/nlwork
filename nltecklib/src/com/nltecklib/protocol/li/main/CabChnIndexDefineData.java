package com.nltecklib.protocol.li.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月16日 下午4:09:58
* 整机通道重新定义
*/
public class CabChnIndexDefineData extends Data implements Configable, Queryable, Responsable {
    
	private Map<Integer,Integer> chnMap = new HashMap<>();
	
	
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
		
		data.add((byte) chnMap.size());
		for(Iterator<Integer> it = chnMap.keySet().iterator() ; it.hasNext() ; ) {
			
			int chnIndex = it.next();
			data.add((byte)chnIndex);
			
			int mapChnIndex = chnMap.get(chnIndex);
			data.add((byte) mapChnIndex);
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			int deviceChnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			int mapChnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			chnMap.put(deviceChnIndex, mapChnIndex);
		}

	}

	@Override
	public Code getCode() {
		
		return MainCode.CabChnIndexDefineCode;
	}
	
	
	public void mapChnIndex(int deviceChnIndex , int mapChnIndex) {
		
		chnMap.put(deviceChnIndex, mapChnIndex);
	}

	public Map<Integer, Integer> getChnMap() {
		return chnMap;
	}
	
	public void clearMap() {
		
		chnMap.clear();
	}

	@Override
	public String toString() {
		return "CabChnIndexDefineData [chnMap=" + chnMap + "]";
	}
	
	
	

}
