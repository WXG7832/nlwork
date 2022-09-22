package com.nltecklib.protocol.fuel.temp;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;

/**
 * 温控板变速泵开关协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardVariablePumpSwitchData extends Data implements BoardNoSupportable, ComponentSupportable, Configable, Queryable, Responsable {
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
	int index = 0;
	data = encodeData;
	state = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return TempCode.VARIABLE_PUMP_SWITCH_CODE;
    }

    @Override
    public String toString() {
	return "TBoardVariablePumpSwitchData [state=" + state + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }

}
