package com.nltecklib.protocol.fuel.temp;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 温控板流量协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardFlowData extends Data implements BoardNoSupportable, ComponentSupportable, Configable, Responsable, Queryable {
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
	data.addAll(Arrays.asList(flowData));
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
	return TempCode.FLOW_CODE;
    }

    @Override
    public String toString() {
	return "TBoardFlowData [flow=" + flow + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }

}
