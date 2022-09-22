package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * PLC压力数据
 * @author Administrator
 *
 */
public class DoubleACTPressureData extends PlcData {
	
	private static final int DEFAULT_READ_ADDRESS = 5970;
	private static final int DEFAULT_WRITE_ADDRESS = 5980;
	private static final String DEFAULT_AREA = "DM";
	
	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex) + "." + dataLength;
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public DoubleACTPressureData() {
		super();
		dataLength = 10;	// 默认读写数据长度
		address = DEFAULT_READ_ADDRESS;		// 默认读数据
		area = DEFAULT_AREA;
	}
	
	public DoubleACTPressureData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
		area = DEFAULT_AREA;
		if(isRead){
			address = DEFAULT_READ_ADDRESS;
		} else {
			address = DEFAULT_WRITE_ADDRESS;
		}
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
		if(isRead){
			address = DEFAULT_READ_ADDRESS;
		} else {
			address = DEFAULT_WRITE_ADDRESS;
		}
	}
	
}
