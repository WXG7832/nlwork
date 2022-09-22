package com.nltecklib.protocol.fuel.protect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.main.KeySwitch;
import com.nltecklib.protocol.fuel.main.KeyValue;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 保护板采集协议数据
 * 
 * @author caichao_tang
 *
 */
public class PBoardPickupData extends Data implements BoardNoSupportable, Responsable, Queryable {
	private List<KeyValue> keyValues = new ArrayList<KeyValue>();
	private List<KeySwitch> keySwitchs = new ArrayList<>();

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

	@Override
	public void encode() {
		data.add((byte) keyValues.size());
		for (KeyValue keyValue : keyValues) {
			data.addAll(Arrays.asList(ProtocolUtil.split(keyValue.component.getNumber(), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValue.value * 10), 2, true)));
		}
		data.add((byte) keySwitchs.size());
		for (int i = 0; i < keySwitchs.size(); i++) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (keySwitchs.get(i).component.getNumber()), 2, true)));
			data.add((byte) (keySwitchs.get(i).state ? State.ON.ordinal() : State.OFF.ordinal()));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int valueNum = data.get(index++);
		for (int i = 0; i < valueNum; i++) {
			KeyValue pressureKeyValue = new KeyValue();
			int componentNo = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			pressureKeyValue.component = Component.get(componentNo);
			double componentValue = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ 10.0;
			index += 2;
			pressureKeyValue.value = componentValue;
			keyValues.add(pressureKeyValue);
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
	}

	@Override
	public Code getCode() {
		return ProtectCode.PICK_UP_CODE;
	}

	@Override
	public String toString() {
		return "PBoardPickupData [keyValues=" + keyValues + ", keySwitchs=" + keySwitchs + "]";
	}

}
