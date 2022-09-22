package com.nltecklib.protocol.fuel.control;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;

/**
 * 控制板储液系统设置协议数据
 * 
 * @author caichao_tang
 *
 */
public class CBoardWaterData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {
    private State state = State.OFF;

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
	return ControlCode.WT_CODE;
    }

    @Override
    public String toString() {
	return "CBoardWaterData [state=" + state + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }

}
