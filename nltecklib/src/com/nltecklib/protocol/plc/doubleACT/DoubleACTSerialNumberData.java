package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 电池组序列号
 * @author Administrator
 *
 */
public class DoubleACTSerialNumberData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 5990;
	private static final String DEFAULT_AREA = "DM";

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex) + "." + dataLength;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public DoubleACTSerialNumberData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		dataLength = 10;	// 默认读写数据长度
	}
	
	public DoubleACTSerialNumberData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}
	
}
