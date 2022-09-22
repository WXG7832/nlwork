package com.nltecklib.protocol.plc.doubleACT;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 电池数量  ----- 好像并不需要本软件读取电池数量
 */
public class DoubleACTBatteryCountData extends PlcData {
	
	private static final int DEFAULT_ADDRESS = 5081;
	private static final String DEFAULT_AREA = "DM";
	
	public DoubleACTBatteryCountData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		dataLength = 1;		// 默认长度
	}

	public byte[] encode(){
		String memory = area + "." + address + "." + dataLength;//内存地址
		return encode(memory, isBit, isRead, null);
	}
	
}
