package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 	分容电池来源
 * @author Administrator
 *
 */
public class PIEF44AGBatterySourceData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 5770;
	private static final String DEFAULT_AREA = "DM";

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex) + "." + dataLength;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44AGBatterySourceData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44AGBatterySourceData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}
	
}
