package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控板压力采集协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class PresPickUpData extends Data implements Queryable, Responsable {

	public static Component[] components = new Component[] { Component.PT_411, Component.PT_431, Component.PT_451,
			Component.PT_452, Component.PT_372, Component.PT_151, Component.PT_412, Component.PT_432 };
	private List<KeyValue> values = new ArrayList<>();

	public List<KeyValue> getValues() {
		return values;
	}

	public void setValues(List<KeyValue> values) {
		this.values = values;
	}

	@Override
	public void encode() {
		for (int i = 0; i < values.size(); i++) {
			Byte[] temperatureData = ProtocolUtil.split((int) (values.get(i).value * 10), 2, true);
			data.addAll(Arrays.asList(temperatureData)); // 编码
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		for (int i = 0, j = 0; i < data.size() && j < components.length; i += 2, j++) {
			double value = ProtocolUtil.compose(data.subList(i, i + 2).toArray(new Byte[0]), true) / 10.0;
			KeyValue keyValue = new KeyValue(components[j], value);
			values.add(keyValue);
		}
	}

	@Override
	public Code getCode() {

		return null;
		// return MainCode.PRES_PICKUP_CODE;
	}

	@Override
	public String toString() {
		return "PresPickUpData [result=" + result + ", orient=" + orient + ", values=" + values + "]";
	}

}
