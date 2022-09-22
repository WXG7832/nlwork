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
 * ÎÂ¿Ø°åÎÂ¶È±¨¾¯Ð­ÒéÊý¾Ý
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TboardTemperatureAlertData extends Data implements BoardNoSupportable, ComponentSupportable, Responsable, Configable, Queryable {
    private double alertMax;
    private double alertMin;
    private double stopMax;
    private double stopMin;

    public double getAlertMax() {
	return alertMax;
    }

    public void setAlertMax(double alertMax) {
	this.alertMax = alertMax;
    }

    public double getAlertMin() {
	return alertMin;
    }

    public void setAlertMin(double alertMin) {
	this.alertMin = alertMin;
    }

    public double getStopMax() {
	return stopMax;
    }

    public void setStopMax(double stopMax) {
	this.stopMax = stopMax;
    }

    public double getStopMin() {
	return stopMin;
    }

    public void setStopMin(double stopMin) {
	this.stopMin = stopMin;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (alertMax * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (alertMin * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stopMax * 10), 2, true))); // ±àÂë
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stopMin * 10), 2, true))); // ±àÂë
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	alertMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	alertMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	stopMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
	index += 2;
	stopMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return TempCode.TEMP_ALERT_CODE;
    }

    @Override
    public String toString() {
	return "TboardTemperatureAlertData [boardNum=" + boardNum + ", alertMax=" + alertMax + ", alertMin=" + alertMin + ", stopMax=" + stopMax + ", stopMin=" + stopMin + "]";
    }

}
