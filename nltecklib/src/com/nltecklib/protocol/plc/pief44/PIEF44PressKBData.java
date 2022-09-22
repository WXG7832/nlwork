package com.nltecklib.protocol.plc.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.model.PressKB;

/**
 * 夹具压力线性KB值
 * @author Administrator
 *
 */
public class PIEF44PressKBData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 4170;
	private static final int DEFAULT_AG_ADDRESS = 4670;
	private static final String DEFAULT_AREA = "DM";
	private List<PressKB> pressKBs = new ArrayList<PressKB>();
	private boolean isIC;
	public boolean isIC() {
		return isIC;
	}

	public void setIC(boolean isIC) {
		this.isIC = isIC;
		if (isIC) {
			address = DEFAULT_IC_ADDRESS;
		}else {
			address = DEFAULT_AG_ADDRESS;
		}
	}
	public List<PressKB> getPressKBs() {
		return pressKBs;
	}

	public void setPressKBs(List<PressKB> pressKBs) {
		this.pressKBs = pressKBs;
	}

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 2) + "." + dataLength * 2;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44PressKBData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44PressKBData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}

	@Override
	public byte[] writeDataDecode() {
		byte[] data = null;
		if(!isRead && !isBit){
			data = new byte[dataLength * 4];
			for (int i = 0; i < pressKBs.size(); i++) {
				int k = pressKBs.get(i).getK();
				byte[] kByteArray = intToByteArray(k);
				data[i*4] = kByteArray[1];
				data[i*4+1] = kByteArray[0];
				int b = pressKBs.get(i).getB();
				byte[] bByteArray = intToByteArray(b);
				data[i*4 + 2] = bByteArray[1];
				data[i*4 + 3] = bByteArray[0];
			}
		}
		return data;
	}
	
	@Override
	public void decode(byte[] data) {
		pressKBs.clear();
		for (int i = 0; i < data.length; i += 4) {
			PressKB pressKB = new PressKB();
			int k = ((data[i] & 0x0ff) << 8) + (data[i + 1] & 0x0ff);
			pressKB.setK(k);
			int b = ((data[i + 2] & 0x0ff) << 8) + (data[i + 3] & 0x0ff);
			pressKB.setB(b);
			pressKBs.add(pressKB);
		}
	}
	
}
