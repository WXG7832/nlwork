package com.nltecklib.protocol.fuel.alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.alert.AlertEnviroment.AlertCode;
import com.nltecklib.protocol.fuel.alert.AlertEnviroment.H2State;
import com.nltecklib.protocol.fuel.main.KeyValue;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ±¨¾¯°å²É¼¯
 * 
 * @author Administrator
 *
 */
@Deprecated
public class ABoardPickupData extends Data implements BoardNoSupportable, Responsable, Queryable {
    private int valueNum;
    private List<KeyValue> keyValues;
    private H2State state;
    private State smogState;

    public int getValueNum() {
	return valueNum;
    }

    public void setValueNum(int valueNum) {
	this.valueNum = valueNum;
    }

    public List<KeyValue> getKeyValues() {
	return keyValues;
    }

    public void setKeyValues(List<KeyValue> keyValues) {
	this.keyValues = keyValues;
    }

    public H2State getState() {
	return state;
    }

    public void setState(H2State state) {
	this.state = state;
    }

    public State getSmogState() {
	return smogState;
    }

    public void setSmogState(State smogState) {
	this.smogState = smogState;
    }

    @Override
    public void encode() {
	data.add((byte) valueNum);
	if (keyValues != null) {
	    for (int i = 0; i < keyValues.size(); i++) {
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).component.getNumber()), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (keyValues.get(i).value * 10), 2, true)));
	    }
	}
	data.add((byte) state.ordinal());
	data.add((byte) smogState.ordinal());

    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	valueNum = ProtocolUtil.getUnsignedByte(data.get(index++));
	keyValues = new ArrayList<KeyValue>();
	for (int i = 0; i < valueNum; i++) {
	    KeyValue keyValue = new KeyValue();
	    keyValue.component=Component.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
	    index += 2;
	    keyValue.value = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	    index += 2;
	    keyValues.add(keyValue);
	}
	state = H2State.values()[data.get(index)];
	index++;
	smogState = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return AlertCode.PICK_UP_CODE;
    }

    @Override
    public String toString() {
	return "ABoardPickupData [valueNum=" + valueNum + ", keyValues=" + keyValues + ", state=" + state + ", smogState=" + smogState + "]";
    }

}
