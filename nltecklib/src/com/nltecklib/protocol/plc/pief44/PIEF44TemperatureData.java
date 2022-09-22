package com.nltecklib.protocol.plc.pief44;

import java.util.HashMap;
import java.util.Map;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 	化成温度数据
 * @author Administrator
 *
 */
public class PIEF44TemperatureData extends PlcData{

	private static final int DEFAULT_IC_ADDRESS = 5000;
	private static final int DEFAULT_AG_ADDRESS = 5400;
	private static final String DEFAULT_AREA = "DM";
	private int boardIndex = 0;		//板层号 从0开始
	
	private static final int BATTERY_COUNT = 36;	// 层板中每一列电池数量
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
		
		String memory = area + "." + (address 
				+ fixtureIndex * 100 + boardIndex) + "." + dataLength;
		
		return encode(memory, false, true, null);
	}
	
	/**
	 * 对板载温度进行平均值计算，得出每一块电池的温度
	 * @param data	PLC返回的数据，层板有37块，数据长度为 37*2；
	 * @return
	 */
	public Map<Integer, Integer> tempDecode(){
		
		Map<Integer, Integer> map = new HashMap<>();	 	// 一个夹具72块电池
		for (int i = 0; i < BATTERY_COUNT; i++) {		
			int value = (datas.get(fixtureIndex + i) + datas.get(fixtureIndex + i + 1)) / 2;
			map.put(i, value);
			map.put(i + BATTERY_COUNT, value);
		}
		return map;
	}

	
	public PIEF44TemperatureData() {
		super();
		address = DEFAULT_IC_ADDRESS;
		area = DEFAULT_AREA;
		dataLength = 37;	// 默认数据长度
	}
	
	public PIEF44TemperatureData(int fixtureIndex) {
		super();
		address = DEFAULT_IC_ADDRESS;
		area = DEFAULT_AREA;
		dataLength = 37;	// 默认数据长度
		this.fixtureIndex = fixtureIndex;
	}

	public PIEF44TemperatureData(int fixtureIndex, int boardIndex, int dataLength) {
		super();
		address = DEFAULT_IC_ADDRESS;
		area = DEFAULT_AREA;
		this.fixtureIndex = fixtureIndex;
		this.boardIndex = boardIndex;
		this.dataLength = dataLength;
	}


	public int getBoardIndex() {
		return boardIndex;
	}

	public void setBoardIndex(int boardIndex) {
		this.boardIndex = boardIndex;
	}
	
}
