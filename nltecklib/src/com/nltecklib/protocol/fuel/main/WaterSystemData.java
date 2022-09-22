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
 * Ö÷¿Ø°å´¢ÒºÏµÍ³¡ª¡ª0x08
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class WaterSystemData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {

    private State systemState = State.OFF;
    private State highLiState = State.OFF;
    private State lowLiState = State.OFF;
    private State pumpState = State.OFF;

    public State getSystemState() {
	return systemState;
    }

    public void setSystemState(State systemState) {
	this.systemState = systemState;
    }

    public State getHighLiState() {
	return highLiState;
    }

    public void setHighLiState(State highLiState) {
	this.highLiState = highLiState;
    }

    public State getLowLiState() {
	return lowLiState;
    }

    public void setLowLiState(State lowLiState) {
	this.lowLiState = lowLiState;
    }

    public State getPumpState() {
	return pumpState;
    }

    public void setPumpState(State pumpState) {
	this.pumpState = pumpState;
    }

    @Override
    public void encode() {

	data.add((byte) systemState.ordinal());
	data.add((byte) highLiState.ordinal());
	data.add((byte) lowLiState.ordinal());
	data.add((byte) pumpState.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	systemState = State.values()[data.get(index)];
	index++;
	highLiState = State.values()[data.get(index)];
	index++;
	lowLiState = State.values()[data.get(index)];
	index++;
	pumpState = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return MainCode.RESERVOIR_SYSTEM_CODE;
    }

    @Override
    public String toString() {
	return "WaterSystemData [componentCode=" + component + ", result=" + result + ", orient=" + orient + ", systemState=" + systemState + ", highLiState=" + highLiState + ", lowLiState=" + lowLiState + ", pumpState=" + pumpState + "]";
    }

}
