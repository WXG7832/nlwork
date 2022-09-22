package com.nltecklib.protocol.fuel.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.ComponentState;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.fuel.main.KeyAlert;
import com.nltecklib.protocol.fuel.main.KeySwitch;
import com.nltecklib.protocol.fuel.main.KeyValue;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 控制板采集协议数据
 * 
 * @author caichao_tang
 *
 */
public class CBoardPickupData extends Data implements Responsable, Queryable {

	private List<KeyValue> keyValues = new ArrayList<>();
	private List<KeySwitch> keySwitchs = new ArrayList<>();
	private List<KeyAlert> keyAlertList = new ArrayList<>();

	public List<KeyAlert> getKeyAlertList() {
		return keyAlertList;
	}

	public void setKeyAlertList(List<KeyAlert> keyAlertList) {
		this.keyAlertList = keyAlertList;
	}

	public List<KeySwitch> getKeySwitchs() {
		return keySwitchs;
	}

	public void setKeySwitchs(List<KeySwitch> keySwitchs) {
		this.keySwitchs = keySwitchs;
	}

	public List<KeyValue> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(List<KeyValue> keyValues) {
		this.keyValues = keyValues;
	}

	@Override
	public void encode() {
		data.add((byte) keyValues.size());
		for (int i = 0; i < keyValues.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).component.getNumber()), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).value * 10), 2, true)));
		}
		data.add((byte) keySwitchs.size());
		for (int i = 0; i < keySwitchs.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keySwitchs.get(i).component.getNumber()), 2, true)));
			data.add((byte) (keySwitchs.get(i).state ? State.ON.ordinal() : State.OFF.ordinal()));
		}
		data.add((byte) keyAlertList.size());
		for (KeyAlert keyAlert : keyAlertList) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keyAlert.component.getNumber(), 2, true)));
			data.add((byte) (keyAlert.state.ordinal()));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		int valueNum = ProtocolUtil.getUnsignedByte(data.get(index++));
		keyValues = new ArrayList<KeyValue>();
		for (int i = 0; i < valueNum; i++) {
			KeyValue keyValue = new KeyValue();
			keyValue.component = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			keyValue.value = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
			index += 2;
			keyValues.add(keyValue);
		}
		int switchNum = ProtocolUtil.getUnsignedByte(data.get(index++));
		keySwitchs = new ArrayList<KeySwitch>();
		for (int i = 0; i < switchNum; i++) {
			KeySwitch keySwitch = new KeySwitch();
			keySwitch.component = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			if (data.get(index) == 0) {
				keySwitch.state = false;
			} else if (data.get(index) == 1) {
				keySwitch.state = true;
			} else {
				throw new RuntimeException("state error index:" + data.get(index));
			}
			index++;
			keySwitchs.add(keySwitch);
		}
		int alertNum = ProtocolUtil.getUnsignedByte(data.get(index++));
		keyAlertList.clear();
		for (int i = 0; i < alertNum; i++) {
			KeyAlert keyAlert = new KeyAlert();
			int componentCode = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
			keyAlert.component = Component.get(componentCode);
			keyAlert.state = ComponentState.values()[data.get(index++)];
			keyAlertList.add(keyAlert);
		}

	}

	@Override
	public Code getCode() {
		return ControlCode.PICK_UP_CODE;
	}

	@Override
	public String toString() {
		return "CBoardPickupData [keyValues=" + keyValues + ", keySwitchs=" + keySwitchs + ", keyAlertList="
				+ keyAlertList + "]";
	}


}
