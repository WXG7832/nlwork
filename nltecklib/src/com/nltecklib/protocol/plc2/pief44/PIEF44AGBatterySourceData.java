package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

/**
 * 	分容电池来源
 * @author Administrator
 *
 */
public class PIEF44AGBatterySourceData extends PlcData{
	private static final int DEFAULT_ADDRESS = 5770;
	private List<Integer> values = new ArrayList<Integer>();

	public void encode(){
		address = DEFAULT_ADDRESS + fixtureIndex ;
		if (values != null && orient == Orient.WRITE) {
			for (int i = 0; i < values.size(); i++) {
				int value =values.get(i);
				byte[] byteArray = intToByteArray(value);
				data.add(byteArray[1]);
				data.add(byteArray[0]);
			}
		}
	}
	
	public PIEF44AGBatterySourceData() {
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
