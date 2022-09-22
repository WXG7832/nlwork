package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.main.MainEnvironment.LoadMode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控电子负载协议数据——0x18
 * 
 * @author caichao_tang
 *
 */
public class ElecLoadData extends Data implements Responsable, Configable, Queryable {

	private State state = State.OFF;// 开关
	private LoadMode mode = LoadMode.CC;
	private int current;
	private int voltage;
	private int power;

    public State getState() {
	return state;
    }

    public void setState(State state) {
	this.state = state;
    }

    public LoadMode getMode() {
	return mode;
    }

    public void setMode(LoadMode mode) {
	this.mode = mode;
    }

    public int getCurrent() {
	return current;
    }

    public void setCurrent(int current) {
	this.current = current;
    }

    public int getVoltage() {
	return voltage;
    }

    public void setVoltage(int voltage) {
	this.voltage = voltage;
    }

    public int getPower() {
	return power;
    }

    public void setPower(int power) {
	this.power = power;
    }

    @Override
    public void encode() {
	data.add((byte) state.ordinal());
	data.add((byte) mode.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltage), 3, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (current), 3, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (power), 3, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (code > State.values().length - 1) {
	    throw new RuntimeException("error state index : " + code);
	}
	state = State.values()[code];

	code = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (code > LoadMode.values().length - 1) {
	    throw new RuntimeException("error loadmode index : " + code);
	}
	mode = LoadMode.values()[code];

	voltage = (int) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	index += 3;
	current = (int) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	index += 3;
	power = (int) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	index += 3;
    }

    @Override
    public Code getCode() {
	return MainCode.ELEC_LOAD_CODE;
    }

    @Override
    public String toString() {
	return "ElecLoadData [state=" + state + ", mode=" + mode + ", current=" + current + ", voltage=" + voltage + ", power=" + power + "]";
    }

}
