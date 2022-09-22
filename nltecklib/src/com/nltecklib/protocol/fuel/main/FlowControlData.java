package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控流量控制协议数据——0x06
 * 
 * @author caichao_tang
 *
 */
public class FlowControlData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {

    private double flow;

    public double getFlow() {
	return flow;
    }

    public void setFlow(double flow) {
	this.flow = flow;
    }

    @Override
    public void encode() {
	Byte[] flowData = ProtocolUtil.split((long) (flow * 10), 2, true);
	data.addAll(Arrays.asList(flowData)); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	double flowRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	flow = flowRecv / 10;
    }

    @Override
    public Code getCode() {
	return MainCode.FLOW_CONTROL_CODE;
    }

    @Override
    public String toString() {
	return "FlowData [componentCode=" + component + ", result=" + result + ", orient=" + orient + ", flow=" + flow + "]";
    }

}
