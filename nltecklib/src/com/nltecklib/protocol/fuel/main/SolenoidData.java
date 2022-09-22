package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;

/**
 * 主控电磁阀协议数据——0x01
 * 
 * @author caichao_tang
 *
 */
public class SolenoidData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {

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
	return MainCode.SOLENOID_VALUE_CODE;
    }

    @Override
    public String toString() {
	return "SolenoidData [componentCode=" + component + ", result=" + result + ", orient=" + orient + ", state=" + state + "]";
    }

}
