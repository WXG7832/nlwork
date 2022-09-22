package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 	读取夹具机压力
 * @author Administrator
 *
 */
public class PIEF44ReadPressureData extends PlcData {
	
	private static final int DEFAULT_IC_ADDRESS = 5750;
	private static final int DEFAULT_AG_ADDRESS = 5754;
	private static final String DEFAULT_AREA = "DM";
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
	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex) + "." + dataLength;
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44ReadPressureData() {
		super();
		dataLength = 4;	// 默认读写数据长度
		address = DEFAULT_IC_ADDRESS;		// 默认读数据
		area = DEFAULT_AREA;
	}
	
	public PIEF44ReadPressureData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
		area = DEFAULT_AREA;
		address = DEFAULT_IC_ADDRESS;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
}
