package com.nltecklib.protocol.fuel.alert;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.alert.AlertEnviroment.AlertCode;

/**
 * ±¨¾¯°å·äÃù
 * 
 * @author Administrator
 *
 */
@Deprecated
public class ABoardBuzzerData extends Data implements BoardNoSupportable, Configable, Responsable, Queryable {
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
	return AlertCode.BUZZER_CODE;
    }

    @Override
    public String toString() {
	return "ABoardBuzzerData [state=" + state + ", data=" + data + ", boardNum=" + boardNum + ", chnNum=" + chnNum + ", componentCode=" + component + ", result=" + result + ", orient=" + orient + "]";
    }

}
