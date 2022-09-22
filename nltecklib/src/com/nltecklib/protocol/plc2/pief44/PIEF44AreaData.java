package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;

/**
 * 区域结果读取
 */
public class PIEF44AreaData extends PlcData {

	private List<Integer> values = new ArrayList<Integer>();;
	private int batteryIndex; // 电池序号

	public void encode() {
		for (int i = 0; i < values.size(); i++) {
			int value = values.get(i);
			byte[] byteArray = intToByteArray(value);
			data.add(byteArray[1]);
			data.add(byteArray[0]);
		}
	}

	public PIEF44AreaData() {
		super();
		area = Area.WR; // 默认地址区域
	}

	public int getBatteryIndex() {
		return batteryIndex;
	}

	public void setBatteryIndex(int batteryIndex) {
		this.batteryIndex = batteryIndex;
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		for (int i = 0; i < data.size() / 2; i++) {
			int value = ((data.get(i * 2) & 0x0ff) << 8) + (data.get(i * 2 + 1) & 0x0ff);
			values.add(value);
		}
	}

}
