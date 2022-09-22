package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;

/**
 * 压紧第五段速度反馈地址
 * @author Administrator
 *
 */
public class PIEF44FifthSpeedBackAddrData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 4130;
	private static final int DEFAULT_AG_ADDRESS = 4630;
	private List<Integer> values = new ArrayList<Integer>();
	
	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}
	
	public void encode(){
		if (isIC) {
			address = DEFAULT_IC_ADDRESS + fixtureIndex * 2;
		}else {
			address = DEFAULT_AG_ADDRESS + fixtureIndex * 2;
		}
		for (int i = 0; i < values.size(); i++) {
			int value = values.get(i);
			byte[] byteArray = intToByteArray(value);
			data.add(byteArray[1]);
			data.add(byteArray[0]);
			data.add(byteArray[3]);
			data.add(byteArray[2]);
		}
	}
	
	public PIEF44FifthSpeedBackAddrData() {
		super();
		area = Area.DM;		// 默认地址区域
	}
	
	
	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		for (int i = 0; i < data.size(); i += 4) {
			int value = ((data.get(i) & 0x0ff) << 8) + (data.get(i + 1) & 0x0ff) + 
					((data.get(i + 2) & 0x0ff) << 24) + ((data.get(i + 3) & 0x0ff) << 16);
			values.add(value);
		}
	}
	
	
}
