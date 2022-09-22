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
 * 主控压力保护协议数据——0x05
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class PressureProtectData extends Data implements ComponentSupportable, Responsable, Configable, Queryable {

    private int alertMax;
    private int alertMin;
    private int stopMax;
    private int stopMin;

    public int getAlertMax() {
	return alertMax;
    }

    public void setAlertMax(int alertMax) {
	this.alertMax = alertMax;
    }

    public int getAlertMin() {
	return alertMin;
    }

    public void setAlertMin(int alertMin) {
	this.alertMin = alertMin;
    }

    public int getStopMax() {
	return stopMax;
    }

    public void setStopMax(int stopMax) {
	this.stopMax = stopMax;
    }

    public int getStopMin() {
	return stopMin;
    }

    public void setStopMin(int stopMin) {
	this.stopMin = stopMin;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) alertMax, 2, true))); // 编码
	data.addAll(Arrays.asList(ProtocolUtil.split((long) alertMin, 2, true))); // 编码
	data.addAll(Arrays.asList(ProtocolUtil.split((long) stopMax, 2, true))); // 编码
	data.addAll(Arrays.asList(ProtocolUtil.split((long) stopMin, 2, true))); // 编码
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	alertMax = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	alertMin = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	stopMax = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	stopMin = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return MainCode.PRESSURE_PROTECT_CODE;
    }

    @Override
    public String toString() {
	return "PressureProtectData [componentCode=" + component + ", result=" + result + ", orient=" + orient + ", alertMax=" + alertMax + ", alertMin=" + alertMin + ", stopMax=" + stopMax + ", stopMin=" + stopMin + "]";
    }

}
