package com.nltecklib.protocol.plc.doubleACT;

import java.util.HashMap;
import java.util.Map;

import com.nltecklib.protocol.plc.PlcData;

/**
 * PLC温度数据
 * @author Administrator
 *
 */
public class DoubleACTTemperatureData extends PlcData{

	private static final int DEFAULT_ADDRESS = 5000;
	private static final String DEFAULT_AREA = "DM";
	private int boardIndex = 0;		//板层号 从0开始
	
	private static final int BATTERY_COUNT = 32;	// 层板中每一列电池数量
	
	public byte[] encode(){
		
		String memory = area + "." + (address 
				+ fixtureIndex * 100 + boardIndex) + "." + dataLength;
		
		return encode(memory, false, true, null);
	}
	
	/**
	 * 对板载温度进行平均值计算，得出每一块电池的温度
	 * @param data	PLC返回的数据，层板有66块，数据长度为 66*2；
	 * @return
	 */
	public Map<Integer, Integer> tempDecode(){
		
		Map<Integer, Integer> map = new HashMap<>();	 	// 一个夹具128块电池
		for (int i = 0; i < BATTERY_COUNT; i++) {		
			int value1 = (datas.get(fixtureIndex + i) + datas.get(fixtureIndex + i + 1)) / 2;
			int value2 = (datas.get(fixtureIndex + i + BATTERY_COUNT + 1) + datas.get(fixtureIndex + i + BATTERY_COUNT + 2)) / 2;
			map.put(i, value1);
			map.put(i + BATTERY_COUNT, value1);
			map.put(i + BATTERY_COUNT * 2, value2);
			map.put(i + BATTERY_COUNT * 3, value2);
		}
		return map;
	}

	
	public DoubleACTTemperatureData() {
		super();
		address = DEFAULT_ADDRESS;
		area = DEFAULT_AREA;
		dataLength = 66;	// 默认数据长度
	}
	
	public DoubleACTTemperatureData(int fixtureIndex) {
		super();
		address = DEFAULT_ADDRESS;
		area = DEFAULT_AREA;
		dataLength = 66;	// 默认数据长度
		this.fixtureIndex = fixtureIndex;
	}

	public DoubleACTTemperatureData(int fixtureIndex, int boardIndex, int dataLength) {
		super();
		address = DEFAULT_ADDRESS;
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
