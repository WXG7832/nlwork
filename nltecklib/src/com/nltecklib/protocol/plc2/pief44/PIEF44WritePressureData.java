package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

/**
 * 	化成夹具机压力
 * @author Administrator
 *
 */
public class PIEF44WritePressureData extends PlcData {
	
	private static final int DEFAULT_AG_ADDRESS = 5764;
	private static final int DEFAULT_IC_ADDRESS = 5760;
	private List<Integer> values = new ArrayList<Integer>();;

	public void encode(){
		if (isIC) {		
			address = DEFAULT_IC_ADDRESS + fixtureIndex ;
		}else {
			address = DEFAULT_AG_ADDRESS + fixtureIndex ;
		}
		if (values != null && orient == Orient.WRITE) {
			for (int i = 0; i < values.size(); i++) {
				int value =values.get(i);
				byte[] byteArray = intToByteArray(value);
				data.add(byteArray[1]);
				data.add(byteArray[0]);
			}
		}
	}
	
	public PIEF44WritePressureData() {
		super();
		area = Area.DM;		// 默认地址区域
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		for (int i = 0; i < data.size(); i += 2) {			
			int value = ((data.get(i) & 0x0ff) << 8) + (data.get(i+1) & 0x0ff);
			values.add(value);
		}
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
}
