package com.nltecklib.protocol.fuel.flow;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流量版校准采集协议数据
 * 
 * @author caichao_tang
 *
 */
public class FBoardCalPickUpData extends Data implements ComponentSupportable, Responsable, Queryable {
    // on表示稳定,off不稳定
    private State ready = State.OFF;
    private double adc;

    public State getReady() {
	return ready;
    }

    public void setReady(State ready) {
	this.ready = ready;
    }

    public double getAdc() {
	return adc;
    }

    public void setAdc(double adc) {
	this.adc = adc;
    }

    @Override
    public void encode() {
	data.add((byte) ready.ordinal());
	Byte[] adcData = ProtocolUtil.split((int) (adc * 100), 3, true);
	data.addAll(Arrays.asList(adcData)); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	ready = State.values()[data.get(index)];
	index++;
	double adcRecv = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	adc = adcRecv / 100;
    }

    @Override
    public Code getCode() {
	return FlowCode.CAL_PICKUP_CODE;
    }

    @Override
    public String toString() {
	return "FBoardCalPickUpData [ready=" + ready + ", adc=" + adc + ", boardNum=" + boardNum + ", componentCode=" + component + ", result=" + result + ", orient=" + orient + "]";
    }

}
