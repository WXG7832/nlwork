package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * EF 流量数据
 * 
 * @author caichao_tang
 *
 */
public class CBoardEFFlowData extends Data implements ComponentSupportable, Queryable, Configable, Responsable {

    /**
     * EF流量值
     */
    private double efFlow;

    public double getEfFlow() {
	return efFlow;
    }

    public void setEfFlow(double efFlow) {
	this.efFlow = efFlow;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (efFlow * 10), 1, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	efFlow = ProtocolUtil.compose(data.subList(index, index + 1).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return ControlCode.EF_FLOW_CODE;
    }

}
