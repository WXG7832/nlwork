package com.nltecklib.protocol.fuel.flow;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流量板流量模式参数协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class FBoardFlowModeParamData extends Data implements  Configable, Responsable, Queryable {
    private double anodeF;
    private double anodeI;
    private double anodeλ;
    private double anodeK;

    private double cathodeF;
    private double cathodeI;
    private double cathodeλ;
    private double cathodeK;

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeF * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeI * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeλ * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (anodeK * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeF * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeI * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeλ * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (cathodeK * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	anodeF = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anodeI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anodeλ = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	anodeK = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeF = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeλ = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	cathodeK = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
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

    public double getAnodeλ() {
	return anodeλ;
    }

    public void setAnodeλ(double anodeλ) {
	this.anodeλ = anodeλ;
    }

    public double getAnodeK() {
	return anodeK;
    }

    public void setAnodeK(double anodeK) {
	this.anodeK = anodeK;
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

    public double getCathodeλ() {
	return cathodeλ;
    }

    public void setCathodeλ(double cathodeλ) {
	this.cathodeλ = cathodeλ;
    }

    public double getCathodeK() {
	return cathodeK;
    }

    public void setCathodeK(double cathodeK) {
	this.cathodeK = cathodeK;
    }

    @Override
    public String toString() {
	return "FBoardFlowModeParamData [anodeF=" + anodeF + ", anodeI=" + anodeI + ", anodeλ=" + anodeλ + ", anodeK=" + anodeK + ", cathodeF=" + cathodeF + ", cathodeI=" + cathodeI + ", cathodeλ=" + cathodeλ + ", cathodeK=" + cathodeK + "]";
    }

    @Override
    public Code getCode() {
	return FlowCode.FLOW_MODE_PARAM_CODE;
    }

}
