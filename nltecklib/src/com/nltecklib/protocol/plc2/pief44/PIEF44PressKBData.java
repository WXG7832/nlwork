package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;
import com.nltecklib.protocol.plc2.pief44.model.PressKB;

/**
 * 夹具压力线性KB值
 * @author Administrator
 *
 */
public class PIEF44PressKBData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 4170;
	private static final int DEFAULT_AG_ADDRESS = 4670;
	private List<PressKB> pressKBs = new ArrayList<PressKB>();

	public List<PressKB> getPressKBs() {
		return pressKBs;
	}

	public void setPressKBs(List<PressKB> pressKBs) {
		this.pressKBs = pressKBs;
	}

	public void encode(){
		if (isIC) {
			address = DEFAULT_IC_ADDRESS + fixtureIndex * 2;
		}else {
			address = DEFAULT_AG_ADDRESS + fixtureIndex * 2;
		}
		for (int i = 0; i < pressKBs.size(); i++) {
			int k = pressKBs.get(i).getK();
			byte[] kByteArray = intToByteArray(k);
			data.add(kByteArray[1]);
			data.add(kByteArray[0]);
			int b = pressKBs.get(i).getB();
			byte[] bByteArray = intToByteArray(b);
			data.add(bByteArray[1]);
			data.add(bByteArray[0]);
		}
	}
	
	public PIEF44PressKBData() {
		super();
		area = Area.DM;		// 默认地址区域
	}

	@Override
	public void decode(List<Byte> encodeData) {
		pressKBs.clear();
		data = encodeData;
		for (int i = 0; i < data.size(); i += 4) {
			PressKB pressKB = new PressKB();
			int k = ((data.get(i) & 0x0ff) << 8) + (data.get(i + 1) & 0x0ff);
			pressKB.setK(k);
			int b = ((data.get(i + 2) & 0x0ff) << 8) + (data.get(i + 3) & 0x0ff);
			pressKB.setB(b);
			pressKBs.add(pressKB);
		}
	}
	
}
