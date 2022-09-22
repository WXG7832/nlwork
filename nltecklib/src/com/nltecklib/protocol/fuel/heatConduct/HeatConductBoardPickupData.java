package com.nltecklib.protocol.fuel.heatConduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.ComponentState;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.main.KeyAlert;
import com.nltecklib.protocol.fuel.main.KeySwitch;
import com.nltecklib.protocol.fuel.main.KeyValue;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 导热罐控制板采集协议
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardPickupData extends Data implements Responsable, Queryable {

	private List<KeyValue> keyValueList = new ArrayList<>();
	private List<KeySwitch> keySwitchList = new ArrayList<>();
	private List<KeyAlert> keyAlertList = new ArrayList<>();

	public List<KeyAlert> getKeyAlertList() {
		return keyAlertList;
	}

	public void setKeyAlertList(List<KeyAlert> keyAlertList) {
		this.keyAlertList = keyAlertList;
	}

	public List<KeyValue> getKeyValueList() {
		return keyValueList;
	}

	public void setKeyValueList(List<KeyValue> keyValueList) {
		this.keyValueList = keyValueList;
	}

	public List<KeySwitch> getKeySwitchList() {
		return keySwitchList;
	}

	public void setKeySwitchList(List<KeySwitch> keySwitchList) {
		this.keySwitchList = keySwitchList;
	}

	@Override
	public void encode() {
		data.add((byte) keyValueList.size());
		for (KeyValue keyValue : keyValueList) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keyValue.component.getNumber(), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValue.value * 10), 2, true)));
		}
		data.add((byte) keySwitchList.size());
		for (KeySwitch keySwitch : keySwitchList) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keySwitch.component.getNumber(), 2, true)));
			data.add((byte) (keySwitch.state ? State.ON.ordinal() : State.OFF.ordinal()));
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
		keyValueList = new ArrayList<KeyValue>();
		for (int i = 0; i < valueNum; i++) {
			KeyValue keyValue = new KeyValue();
			int componentCode = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			keyValue.component = Component.get(componentCode);
			index += 2;
			keyValue.value = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
			index += 2;
			keyValueList.add(keyValue);
		}
		int switchNum = ProtocolUtil.getUnsignedByte(data.get(index++));
		keySwitchList = new ArrayList<KeySwitch>();
		for (int i = 0; i < switchNum; i++) {
			KeySwitch keySwitch = new KeySwitch();
			int componentCode = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			keySwitch.component = Component.get(componentCode);
			index += 2;
			if (data.get(index) == 0) {
				keySwitch.state = false;
			} else if (data.get(index) == 1) {
				keySwitch.state = true;
			} else {
				throw new RuntimeException("此字节数据无法对应开关量：" + data.get(index));
			}
			index++;
			keySwitchList.add(keySwitch);
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
		return HeatConductBoardFunctionCode.PICKUP;
	}

	@Override
	public String toString() {
		return "HeatConductBoardPickupData [keyValueList=" + keyValueList + ", keySwitchList=" + keySwitchList
				+ ", keyAlertList=" + keyAlertList + "]";
	}

}
