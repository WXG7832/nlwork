package com.nltecklib.protocol.fuel.temp;

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
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 温控板采集协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardPickupData extends Data implements BoardNoSupportable, Responsable, Queryable {
	private int valueNum;
	private List<KeyValue> keyValues;
	private int switchNum;
	private List<KeySwitch> keySwitchs;

	public int getValueNum() {
		return valueNum;
	}

	public void setValueNum(int valueNum) {
		this.valueNum = valueNum;
	}

	public int getSwitchNum() {
		return switchNum;
	}

	public void setSwitchNum(int switchNum) {
		this.switchNum = switchNum;
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
		data.add((byte) valueNum);
		if (keyValues != null) {
			for (int i = 0; i < keyValues.size(); i++) {
				data.addAll(
						Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).component.getNumber()), 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).value * 10), 2, true)));
			}
		}
		data.add((byte) switchNum);
		if (keyValues != null) {
			for (int i = 0; i < keySwitchs.size(); i++) {
				data.addAll(
						Arrays.asList(ProtocolUtil.split((long) (keySwitchs.get(i).component.getNumber()), 2, true)));
				data.add((byte) (keySwitchs.get(i).state ? State.ON.ordinal() : State.OFF.ordinal()));
			}
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		valueNum = ProtocolUtil.getUnsignedByte(data.get(index++));
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
		switchNum = ProtocolUtil.getUnsignedByte(data.get(index++));
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
		return TempCode.PICK_UP_CODE;
	}

	@Override
	public String toString() {
		return "TBoardPickupData [boardNum=" + boardNum + ", valueNum=" + valueNum + ", keyValues=" + keyValues
				+ ", switchNum=" + switchNum + ", keySwitchs=" + keySwitchs + "]";
	}
}
