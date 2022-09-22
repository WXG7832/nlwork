package com.nltecklib.protocol.plc.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 预压位坐标地址
 * @author Administrator
 *
 */
public class PIEF44PrePressAddrData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 4000;
	private static final int DEFAULT_AG_ADDRESS = 4500;
	private static final String DEFAULT_AREA = "DM";
	private boolean isIC;
	private List<Integer> values = new ArrayList<Integer>();
	
	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

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
	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 2) + "." + dataLength * 2;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44PrePressAddrData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44PrePressAddrData(int fixtureIndex, int dataLength, boolean isRead) {
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
			for (int i = 0; i < dataLength; i++) {
				int value = values.get(i);
				byte[] byteArray = intToByteArray(value);
				data[i*4] = byteArray[1];
				data[i*4+1] = byteArray[0];
				data[i*4+2] = byteArray[3];
				data[i*4+3] = byteArray[2];
			}
		}
		return data;
	}
	
	@Override
	public void decode(byte[] data) {
		for (int i = 0; i < data.length; i += 4) {
			int value = ((data[i] & 0x0ff) << 8) + (data[i + 1] & 0x0ff) + 
					((data[i + 2] & 0x0ff) << 24) + ((data[i + 3] & 0x0ff) << 16);
			values.add(value);
		}
	}
	
	
}
