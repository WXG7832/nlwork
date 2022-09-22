package com.nltecklib.protocol.fuel.control;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;

/**
 * µªÆø×´Ì¬
 * 
 * @author Administrator
 *
 */
@Deprecated
public class CBoardN2StateData extends Data implements BoardNoSupportable, Responsable, Queryable {
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
	return ControlCode.N2_STATE_CODE;
    }

    @Override
    public String toString() {
	return "CBoardN2StateData [boardNum=" + boardNum + ", state=" + state + "]";
    }

}
