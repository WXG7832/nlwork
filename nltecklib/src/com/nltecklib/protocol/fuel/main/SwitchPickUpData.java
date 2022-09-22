package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * 主控开关量采集协议数据——0x13
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class SwitchPickUpData extends Data implements Queryable, Responsable {
	/**
	 * 要采集的开关量器件
	 */
	public static Component[] components = new Component[] { Component.SOV_102, Component.SOV_112, Component.SOV_121,
			Component.SOV_152, Component.SOV_153, Component.SOV_162, Component.SOV_341, Component.SOV_311,
			Component.SOV_351, Component.SOV_455, Component.SOV_456, Component.SOV_457, Component.SOV_458,
			Component.SOV_667, Component.WT_125, Component.LI_123, Component.LI_124, Component.PMP_122,
			Component.WT_144, Component.LI_142, Component.LI_143, Component.PMP_141, Component.SOV_461 };
	private List<KeySwitch> values = new ArrayList<>();

	public List<KeySwitch> getValues() {
		return values;
	}

	public void setValues(List<KeySwitch> values) {
		this.values = values;
	}

	@Override
	public void encode() {
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).state) {
				data.add((byte) 1);
			} else {
				data.add((byte) 0);
			}
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		for (int i = 0, j = 0; i < data.size() && j < components.length; i++, j++) {
			KeySwitch keySwitch = new KeySwitch();
			keySwitch.component = components[i];
			if (data.get(i) == 0) {
				keySwitch.state = false;
			} else if (data.get(i) == 1) {
				keySwitch.state = true;
			} else {
				throw new RuntimeException("state error index:" + data.get(i) + ", component:" + components[i]);
			}
			values.add(keySwitch);
		}

	}

	@Override
	public Code getCode() {
		return null;
		// return MainCode.SWITCH_PICKUP_CODE;
	}

	@Override
	public String toString() {
		return "SwitchPickUpData [result=" + result + ", orient=" + orient + ", components="
				+ Arrays.toString(components) + ", values=" + values + "]";
	}

}