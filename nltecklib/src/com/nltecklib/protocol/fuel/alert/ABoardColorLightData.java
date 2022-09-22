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
 * ±¨¾¯°åÈýÉ«µÆ
 * 
 * @author Administrator
 *
 */
@Deprecated
public class ABoardColorLightData extends Data implements BoardNoSupportable, Configable, Responsable, Queryable {

    private State lightRed = State.OFF;
    private State lightYellow = State.OFF;
    private State lightGreen = State.OFF;

    public ABoardColorLightData() {
	super();
    }

    public ABoardColorLightData(State lightRed, State lightYellow, State lightGreen) {
	super();
	this.lightRed = lightRed;
	this.lightYellow = lightYellow;
	this.lightGreen = lightGreen;
    }

    public State getLightRed() {
	return lightRed;
    }

    public void setLightRed(State lightRed) {
	this.lightRed = lightRed;
    }

    public State getLightYellow() {
	return lightYellow;
    }

    public void setLightYellow(State lightYellow) {
	this.lightYellow = lightYellow;
    }

    public State getLightGreen() {
	return lightGreen;
    }

    public void setLightGreen(State lightGreen) {
	this.lightGreen = lightGreen;
    }

    private byte encodeLight() {
	byte result = 0x00;
	if (lightRed == State.ON) {
	    result |= 1;
	}
	if (lightYellow == State.ON) {
	    result |= 1 << 1;
	}
	if (lightGreen == State.ON) {
	    result |= 1 << 2;
	}
	return result;
    }

    private void decodeLight(byte code) {
	lightRed = (code & 1) > 0 ? State.ON : State.OFF;
	lightYellow = (code & 1 << 1) > 0 ? State.ON : State.OFF;
	lightGreen = (code & 1 << 2) > 0 ? State.ON : State.OFF;
    }

    @Override
    public void encode() {
	data.add((byte) encodeLight());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	decodeLight(data.get(index++));
    }

    @Override
    public Code getCode() {
	return AlertCode.COLOR_LIGHT_CODE;
    }

    @Override
    public String toString() {
	return "ABoardColorLightData [lightRed=" + lightRed + ", lightYellow=" + lightYellow + ", lightGreen=" + lightGreen + "]";
    }

}
