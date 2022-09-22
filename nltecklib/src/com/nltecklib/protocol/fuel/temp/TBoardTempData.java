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
 * 温控板温度协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardTempData extends Data implements BoardNoSupportable, ComponentSupportable, Responsable, Configable, Queryable {
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
	return TempCode.TEMP_CODE;
    }

    @Override
    public String toString() {
	return "TBoardTempData [temperature=" + temperature + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }

}