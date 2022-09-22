package com.nltecklib.protocol.fuel.flow;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流量板变速泵协议数据
 * 
 * @author caichao_tang
 *
 */
public class FBoardVariablePmpData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {
    private double rate;

    public double getRate() {
	return rate;
    }

    public void setRate(double rate) {
	this.rate = rate;
    }

    @Override
    public void encode() {
	Byte[] rateData = ProtocolUtil.split((long) (rate * 10), 2, true);
	data.addAll(Arrays.asList(rateData)); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	rate = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return FlowCode.VARIABLE_PUMP_CODE;
    }

    @Override
    public String toString() {
	return "FBoardVariablePmpData [rate=" + rate + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }
}