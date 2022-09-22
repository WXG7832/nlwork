package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 测试结果数据
 */
public class DoubleACTTestResultData extends PlcData{
	
	public enum TestResult{
		
		None, Good, FormationBad, CapacityBad;
		
		@Override
		public String toString(){
			switch (this) {
			case Good:
				return "良品";
			case FormationBad:
				return "化成不良";
			case CapacityBad:
				return "容量不良";

			default:
				break;
			}
			return "";
		}
		
	}
	
	private static final int DEFAULT_ADDRESS = 6000;
	private static final String DEFAULT_AREA = "DM";
	private int batteryIndex;

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 200 + batteryIndex) 
				+ "." + dataLength;
		fixtureIndex = 0;
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}

	public DoubleACTTestResultData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址	
		dataLength = 128;		// 默认长度
	}

	public DoubleACTTestResultData(int fixtureIndex, int batteryIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
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
	
}
