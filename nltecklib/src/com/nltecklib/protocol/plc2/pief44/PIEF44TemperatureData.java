package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;


/**
 * 	化成温度数据
 * @author Administrator
 *
 */
public class PIEF44TemperatureData extends PlcData{

	private static final int DEFAULT_IC_ADDRESS = 5000;
	private static final int DEFAULT_AG_ADDRESS = 5400;
	private static final int DEFAULT_LENGTH = 37;
	private int boardIndex = 0;		//板层号 从0开始
	private Map<Integer, Integer> temps = new HashMap<>();
	
	private static final int BATTERY_COUNT = 36;	// 层板中每一列电池数量
	
	public void encode(){
		if (isIC) {
			address = DEFAULT_IC_ADDRESS + fixtureIndex * 100 + boardIndex;
		}else {
			address = DEFAULT_AG_ADDRESS + fixtureIndex * 100 + boardIndex;
		}
	}
	
	public PIEF44TemperatureData() {
		super();
		area = Area.DM;
		datalength = DEFAULT_LENGTH;
	}
	

	public int getBoardIndex() {
		return boardIndex;
	}

	public void setBoardIndex(int boardIndex) {
		this.boardIndex = boardIndex;
	}
	
	public Map<Integer, Integer> getTemps() {
		return temps;
	}

	public void setTemps(Map<Integer, Integer> temps) {
		this.temps = temps;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		temps.clear();
		data = encodeData;
		List<Integer> results = new ArrayList<>();
		for (int i = 0; i < data.size(); i += 2) {
			int value = ((data.get(i) & 0x0ff) << 8) + (data.get(i+1) & 0x0ff);
			results.add(value);
		}
		for (int i = 0; i < BATTERY_COUNT; i++) {
			int value = (results.get(i) +results.get(i + 1)) / 2;
			temps.put(i, value);
			temps.put(i + BATTERY_COUNT, value);
		}
	}
	
}
