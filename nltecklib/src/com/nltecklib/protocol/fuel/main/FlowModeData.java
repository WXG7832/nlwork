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
 * Ö÷¿ØÁ÷Á¿Ä£Ê½Ð­ÒéÊý¾Ý¡ª¡ª0X17
 * 
 * @author caichao_tang
 *
 */
public class FlowModeData extends Data implements Configable, Responsable, Queryable {
    private FlowMode anodeMode;
    private double anodeF;
    private double anodeI;
    private double anode¦Ë;
    private double anodeK;

    private FlowMode cathodeMode;
    private double cathodeF;
    private double cathodeI;
    private double cathode¦Ë;
    private double cathodeK;

    @Override
    public void encode() {
	data.add((byte) anodeMode.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeF * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeI * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anode¦Ë * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeK * 10), 2, true))); // ±àÂë
	data.add((byte) cathodeMode.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeF * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeI * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathode¦Ë * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeK * 10), 2, true))); // ±àÂë
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	anodeMode = FlowMode.values()[data.get(index)];
	index++;
	anodeF = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anodeI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anode¦Ë = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anodeK = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeMode = FlowMode.values()[data.get(index)];
	index++;
	cathodeF = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathode¦Ë = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeK = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
    }

    @Override
    public Code getCode() {
	return MainCode.FLOW_MODE_CODE;
    }

    @Override
    public String toString() {
	return "FlowModeData [result=" + result + ", orient=" + orient + "anodeMode=" + anodeMode + ", anodeF=" + anodeF + ", anodeI=" + anodeI + ", anode¦Ë=" + anode¦Ë + ", anodeK=" + anodeK + ", cathodeMode=" + cathodeMode + ", cathodeF=" + cathodeF + ", cathodeI=" + cathodeI + ", cathode¦Ë=" + cathode¦Ë + ", cathodeK=" + cathodeK + "]";
    }

    public FlowMode getAnodeMode() {
	return anodeMode;
    }

    public void setAnodeMode(FlowMode anodeMode) {
	this.anodeMode = anodeMode;
    }

    public double getAnodeF() {
	return anodeF;
    }

    public void setAnodeF(double anodeF) {
	this.anodeF = anodeF;
    }

    public double getAnodeI() {
	return anodeI;
    }

    public void setAnodeI(double anodeI) {
	this.anodeI = anodeI;
    }

    public double getAnode¦Ë() {
	return anode¦Ë;
    }

    public void setAnode¦Ë(double anode¦Ë) {
	this.anode¦Ë = anode¦Ë;
    }

    public double getAnodeK() {
	return anodeK;
    }

    public void setAnodeK(double anodeK) {
	this.anodeK = anodeK;
    }

    public FlowMode getCathodeMode() {
	return cathodeMode;
    }

    public void setCathodeMode(FlowMode cathodeMode) {
	this.cathodeMode = cathodeMode;
    }

    public double getCathodeF() {
	return cathodeF;
    }

    public void setCathodeF(double cathodeF) {
	this.cathodeF = cathodeF;
    }

    public double getCathodeI() {
	return cathodeI;
    }

    public void setCathodeI(double cathodeI) {
	this.cathodeI = cathodeI;
    }

    public double getCathode¦Ë() {
	return cathode¦Ë;
    }

    public void setCathode¦Ë(double cathode¦Ë) {
	this.cathode¦Ë = cathode¦Ë;
    }

    public double getCathodeK() {
	return cathodeK;
    }

    public void setCathodeK(double cathodeK) {
	this.cathodeK = cathodeK;
    }

}
