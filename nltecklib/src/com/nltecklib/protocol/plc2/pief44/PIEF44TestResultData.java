package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;

/**
 * 测试结果数据
 */
public class PIEF44TestResultData extends PlcData {

	public enum TestResult {

		FormationBad(00), CapacityBad(01), Good(0x3);

		private int code;

		private TestResult(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}

		@Override
		public String toString() {
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

	private static final int DEFAULT_ADDRESS_AG = 5800;
	private static final int DEFAULT_ADDRESS_IC = 6300;
	private List<Integer> values = new ArrayList<Integer>();;
	private int batteryIndex; // 电池序号

	public void encode() {
		if (fixtureIndex < 4) {
			address = DEFAULT_ADDRESS_IC + fixtureIndex * 100 + batteryIndex;
		} else {
			address = DEFAULT_ADDRESS_AG + fixtureIndex * 100 + batteryIndex;
		}
		for (int i = 0; i < values.size(); i++) {
			int value = values.get(i);
			byte[] byteArray = intToByteArray(value);
			data.add(byteArray[1]);
			data.add(byteArray[0]);
		}
	}

	public PIEF44TestResultData() {
		super();
		area = Area.DM; // 默认地址区域
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
		for (int i = 0; i < data.size(); i += 2) {
			int value = ((data.get(i) & 0x0ff) << 8) + (data.get(i + 1) & 0x0ff);
			values.add(value);
		}
	}

}
