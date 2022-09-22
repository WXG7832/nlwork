package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 常温箱温度读取
 * @author wavy_zheng
 * 2020年3月5日
 *
 */
public class TempProbeData extends Data implements Queryable, Responsable {
    
	private List<Double> tempList = new ArrayList<Double>();
	
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
	
	public void appendTemp(double temp) {
		
		tempList.add(temp);
	}
	
	public void clearTempList() {
		
		tempList.clear();
	}
	
	

	public List<Double> getTempList() {
		return tempList;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) tempList.size());
		for(int n = 0 ; n < tempList.size() ; n++) {
			
			short t = (short)(tempList.get(n) * 10);
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(t & 0xffff), 2,true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			short temp = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			tempList.add((double)temp / 10);
			index += 2;
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.TempProbeCode;
	}

}
