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
 * 流量板温度协议数据
 * 
 * @author caichao_tang
 *
 */
public class FBoardTemperatureData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    /**
     * 温度值
     */
    private double temperature;

    public double getTemperature() {
	return temperature;
    }

    public void setTemperature(double temperature) {
	this.temperature = temperature;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((int) (temperature * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	temperature = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return FlowCode.TEMPERATURE_CODE;
    }

}
