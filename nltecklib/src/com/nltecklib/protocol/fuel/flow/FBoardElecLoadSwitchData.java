package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;

/**
 * 流量板电子负载开关协议数据
 * 
 * @author caichao_tang
 *
 */
public class FBoardElecLoadSwitchData extends Data implements Configable, Queryable, Responsable {
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
	int index = 0;
	data = encodeData;
	state = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return FlowCode.ELEC_LOAD_SWITCH_CODE;
    }

    @Override
    public String toString() {
	return "FBoardElecLoadSwitchData [state=" + state + "]";
    }

}
