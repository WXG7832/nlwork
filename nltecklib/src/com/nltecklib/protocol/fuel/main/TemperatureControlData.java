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
 * 主控板温度控制协议数据——0x02
 * 
 * @author caichao_tang
 *
 */
public class TemperatureControlData extends Data implements ComponentSupportable, Responsable, Configable, Queryable {

    private double temperature;

    public double getTemperature() {
	return temperature;
    }

    public void setTemperature(double temperature) {
	this.temperature = temperature;
    }

    @Override
    public void encode() {
	Byte[] temperatureData = ProtocolUtil.split((int) (temperature * 10), 2, true);
	data.addAll(Arrays.asList(temperatureData)); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	double temperatureRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	temperature = temperatureRecv / 10;
    }

    @Override
    public Code getCode() {
	return MainCode.TEMPERATURE_CONTROL_CODE;
    }

    @Override
    public String toString() {
	return "TempData [componentCode=" + component + ", result=" + result + ", orient=" + orient + ", temperature=" + temperature + "]";
    }

}