package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.FlowMode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 设置协议数据类 0x22
 * 
 * @author caichao_tang
 *
 */
public class SetData extends Data implements Configable, Responsable, Queryable {
    private FlowMode anodeFlowMode = FlowMode.STATIC_FLOW;
    /**
     * 阳极电流阀值 mA
     */
    private int anodeI0;
    /**
     * 阳极H2最低流量 0.1 L/min
     */
    private double anodeF0;
    /**
     * 阳极计量比 0.1
     */
    private double anodeλ;
    /**
     * 阳极Ka系数 0.1
     */
    private double anodeKa;
    private FlowMode cathodeFlowMode = FlowMode.STATIC_FLOW;
    /**
     * 阴极电流阀值 mA
     */
    private int cathodeI0;
    /**
     * 阴极最低流量 0.1 L/min
     */
    private double cathodeF0;
    /**
     * 阴极计量比 0.1
     */
    private double cathodeλ;
    /**
     * 阴极Kb系数 0.1
     */
    private double cathodeKa;
    private int n2Time;
    private int usedChnNum;
    private int n2Delay;
    private int transducerDelay;

    public FlowMode getAnodeFlowMode() {
	return anodeFlowMode;
    }

    public void setAnodeFlowMode(FlowMode anodeFlowMode) {
	this.anodeFlowMode = anodeFlowMode;
    }

    public int getAnodeI0() {
	return anodeI0;
    }

    public void setAnodeI0(int anodeI0) {
	this.anodeI0 = anodeI0;
    }

    public double getAnodeF0() {
	return anodeF0;
    }

    public void setAnodeF0(double anodeF0) {
	this.anodeF0 = anodeF0;
    }

    public double getAnodeλ() {
	return anodeλ;
    }

    public void setAnodeλ(double anodeλ) {
	this.anodeλ = anodeλ;
    }

    public double getAnodeKa() {
	return anodeKa;
    }

    public void setAnodeKa(double anodeKa) {
	this.anodeKa = anodeKa;
    }

    public FlowMode getCathodeFlowMode() {
	return cathodeFlowMode;
    }

    public void setCathodeFlowMode(FlowMode cathodeFlowMode) {
	this.cathodeFlowMode = cathodeFlowMode;
    }

    public int getCathodeI0() {
	return cathodeI0;
    }

    public void setCathodeI0(int cathodeI0) {
	this.cathodeI0 = cathodeI0;
    }

    public double getCathodeF0() {
	return cathodeF0;
    }

    public void setCathodeF0(double cathodeF0) {
	this.cathodeF0 = cathodeF0;
    }

    public double getCathodeλ() {
	return cathodeλ;
    }

    public void setCathodeλ(double cathodeλ) {
	this.cathodeλ = cathodeλ;
    }

    public double getCathodeKa() {
	return cathodeKa;
    }

    public void setCathodeKa(double cathodeKa) {
	this.cathodeKa = cathodeKa;
    }

    public int getN2Time() {
	return n2Time;
    }

    public void setN2Time(int n2Time) {
	this.n2Time = n2Time;
    }

    public int getUsedChnNum() {
	return usedChnNum;
    }

    public void setUsedChnNum(int usedChnNum) {
	this.usedChnNum = usedChnNum;
    }

    public int getN2Delay() {
	return n2Delay;
    }

    public void setN2Delay(int n2Delay) {
	this.n2Delay = n2Delay;
    }

    public int getTransducerDelay() {
	return transducerDelay;
    }

    public void setTransducerDelay(int transducerDelay) {
	this.transducerDelay = transducerDelay;
    }

    @Override
    public void encode() {
	data.add((byte) anodeFlowMode.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split(anodeI0, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (anodeF0 * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (anodeλ * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (anodeKa * 10), 2, true)));

	data.add((byte) cathodeFlowMode.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split(cathodeI0, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (cathodeF0 * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (cathodeλ * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (cathodeKa * 10), 2, true)));

	data.addAll(Arrays.asList(ProtocolUtil.split(n2Time, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(usedChnNum, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(n2Delay, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(transducerDelay, 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;

	anodeFlowMode = FlowMode.values()[data.get(index++)];
	anodeI0 = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	anodeF0 = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
	anodeλ = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
	anodeKa = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;

	cathodeFlowMode = FlowMode.values()[data.get(index++)];
	cathodeI0 = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	cathodeF0 = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
	cathodeλ = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
	cathodeKa = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;

	n2Time = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	usedChnNum = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	n2Delay = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	transducerDelay = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return MainCode.SET_CODE;
    }

    @Override
    public String toString() {
	return "SetData [anodeFlowMode=" + anodeFlowMode + ", anodeI0=" + anodeI0 + ", anodeF0=" + anodeF0 + ", anodeλ=" + anodeλ + ", anodeKa=" + anodeKa + ", cathodeFlowMode=" + cathodeFlowMode + ", cathodeI0=" + cathodeI0 + ", cathodeF0=" + cathodeF0 + ", cathodeλ=" + cathodeλ + ", cathodeKa=" + cathodeKa + ", n2Time=" + n2Time + ", usedChnNum=" + usedChnNum + ", n2Delay=" + n2Delay + ", tranducerDelay=" + transducerDelay + "]";
    }

}
