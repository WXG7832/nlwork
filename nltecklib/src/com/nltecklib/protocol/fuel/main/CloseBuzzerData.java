package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;

/**
 * 主控蜂鸣器状态协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class CloseBuzzerData extends Data implements Configable, Queryable, Responsable {
    private State state;

    public State getState() {
	return state;
    }

    public void setState(State state) {
	this.state = state;
    }

    @Override
    public void encode() {
	data.add((byte) state.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	state = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {

    	return null;
    	//return MainCode.CLOSE_BUZZER_CODE;
    }

    @Override
    public String toString() {
	return "CloseBuzzerData [state=" + state + "]";
    }

}
