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
 * 主控板温度采集协议数据——0x0E
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TempPickUpData extends Data implements Queryable, Responsable {
	/**
	 * 要进行温度采集的器件
	 */
	public static Component[] components = new Component[] { Component.TC_413, Component.TC_433, Component.TC_453,
			Component.TC_454, Component.TC_481, Component.TC_482, Component.TC_483, Component.TC_484, Component.TC_314,
			Component.TC_315, Component.TC_317, Component.TC_332, Component.TC_333, Component.TC_355, Component.TC_357,
			Component.TC_318, Component.TC_382, Component.TC_383, Component.TC_661, Component.TC_633, Component.TC_692,
			Component.TC_414, Component.TC_434 };
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
		// return MainCode.TEMP_PICKUP_CODE;
	}

	@Override
	public String toString() {
		return "TempPickUpData [components=" + Arrays.toString(components) + ", values=" + values + ", result=" + result
				+ ", orient=" + orient + "]";
	}

}
