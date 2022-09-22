package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 下料水车数据
 */
public class DoubleACTWaterwheelData extends PlcData {
	
	private static final int DEFAULT_ADDRESS = 8000;
	private static final String DEFAULT_AREA = "DM";
	private int batteryIndex;	// 电池序号
	
	public byte[] encode(){
		String memory = area + "." + (address + batteryIndex) + "." + dataLength;
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}

	public DoubleACTWaterwheelData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址		
		dataLength = 118;		// 默认长度
	}

	public DoubleACTWaterwheelData(int batteryIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址	
		this.batteryIndex = batteryIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}

	public int getBatteryIndex() {
		return batteryIndex;
	}

	public void setBatteryIndex(int batteryIndex) {
		this.batteryIndex = batteryIndex;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	
}
