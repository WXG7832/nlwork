package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 测试结果数据
 */
public class PIEF44TestResultData extends PlcData{
	
	public enum TestResult{
		
		FormationBad(00), CapacityBad(01),Good(0x3);
		
		private int code;
		private TestResult(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
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
	
	private static final int DEFAULT_ADDRESS = 5800;
	private static final String DEFAULT_AREA = "DM";
	private int batteryIndex;

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 100 + batteryIndex) 
				+ "." + dataLength;
		fixtureIndex = 0;
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}

	public PIEF44TestResultData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址	
		dataLength = 72;		// 默认长度
	}

	public PIEF44TestResultData(int fixtureIndex, int batteryIndex, int dataLength, boolean isRead) {
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
