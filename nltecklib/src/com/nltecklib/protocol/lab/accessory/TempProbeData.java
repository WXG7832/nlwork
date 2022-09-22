package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 温度读取
 * @author Administrator
 *
 */
public class TempProbeData extends Data implements Queryable,Responsable {
	
	//private int probeCount; //探头数量
	private List<Double> tempValue = new ArrayList<Double>(); //温度
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void encode() {
		
		
		//探头数量
		data.add((byte) tempValue.size());
		for (int i = 0; i < tempValue.size(); i++) {
			
			//温度值
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(tempValue.get(i) * 10), 2,true)));
		}	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		//探头数量
		int probeCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		tempValue.clear();
		for (int i = 0; i < probeCount; i++) {
			
			tempValue.add( (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10 );
			index += 2;
		}	
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.TempProbeCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public List<Double> getTempValue() {
		return tempValue;
	}

	public void setTempValue(List<Double> tempValue) {
		this.tempValue = tempValue;
	}
	@Override
	public String toString() {
		return "TempProbeData [tempValue=" + tempValue + "]";
	}
	
	
	
	
}
