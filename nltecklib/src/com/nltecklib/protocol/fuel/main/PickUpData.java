package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.ComponentState;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控板采集上报控制
 * 
 * @author guofang_ma
 *
 */
public class PickUpData extends Data implements Configable, Responsable, Alertable {

	private List<KeyValue> keyValues = new ArrayList<>();// 数值量，2字节，单位0.1
	private List<KeySwitch> keySwitchs = new ArrayList<>();// 开关量，1字节
	private List<KeyAlert> keyAlerts = new ArrayList<>();// 状态量，1字节

	public List<KeyValue> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(List<KeyValue> keyValues) {
		this.keyValues = keyValues;
	}

	public List<KeySwitch> getKeySwitchs() {
		return keySwitchs;
	}

	public void setKeySwitchs(List<KeySwitch> keySwitchs) {
		this.keySwitchs = keySwitchs;
	}

	public List<KeyAlert> getKeyAlerts() {
		return keyAlerts;
	}

	public void setKeyAlerts(List<KeyAlert> keyAlerts) {
		this.keyAlerts = keyAlerts;
	}

	@Override
	public void encode() {

		data.add((byte) keyValues.size());
		for (int i = 0; i < keyValues.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keyValues.get(i).component.getNumber(), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((int) (keyValues.get(i).value * 10), 2, true)));
		}

		data.add((byte) keySwitchs.size());
		for (int i = 0; i < keySwitchs.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keySwitchs.get(i).component.getNumber(), 2, true)));
			data.add((byte) (keySwitchs.get(i).state ? 1 : 0));
		}
		data.add((byte) keyAlerts.size());
		for (int i = 0; i < keyAlerts.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keyAlerts.get(i).component.getNumber(), 2, true)));
			data.add((byte) (keyAlerts.get(i).state.ordinal()));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			KeyValue kv = new KeyValue();
			kv.component = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			kv.value = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
			index += 2;
			keyValues.add(kv);
		}

		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			KeySwitch ks = new KeySwitch();
			ks.component = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			ks.state = data.get(index++) == 1;
			keySwitchs.add(ks);
		}

		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			KeyAlert ka = new KeyAlert();
			ka.component = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			ka.state = ComponentState.values()[data.get(index++)];
			keyAlerts.add(ka);
		}
	}

	@Override
	public Code getCode() {

		return MainCode.PICKUP_CODE;
	}

	@Override
	public String toString() {
		return "PickUpData [keyValues=" + keyValues + ", keySwitchs=" + keySwitchs + ", keyAlerts=" + keyAlerts + "]";
	}

}
