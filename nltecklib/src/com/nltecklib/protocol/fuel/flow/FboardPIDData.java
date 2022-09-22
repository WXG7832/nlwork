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
 * 流量板PID协议数据
 * 
 * @author caichao_tang
 *
 */
public class FboardPIDData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    private double p;
    private double i;
    private double d;

    public double getP() {
	return p;
    }

    public void setP(double p) {
	this.p = p;
    }

    public double getI() {
	return i;
    }

    public void setI(double i) {
	this.i = i;
    }

    public double getD() {
	return d;
    }

    public void setD(double d) {
	this.d = d;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (p * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (i * 10), 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (d * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	p = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	i = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	d = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return FlowCode.PID_CODE;
    }

}
