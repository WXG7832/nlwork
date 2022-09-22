package com.nltecklib.protocol.fuel.voltage;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板报警值设定-0x05
 * 
 * @author caichao_tang
 *
 */
public class VBoardAlertValueSetData extends Data implements BoardNoSupportable, Responsable, Queryable, Configable {
    private int usedChnNum;//使用通道数
    private double anodeVoltageUpLimit;// 0.01mV
    private double anodeVoltageDownLimit;// 0.01mV
    private double cathodeVoltageUpLimit;// 0.01mV
    private double cathodeVoltageDownLimit;// 0.01mV
    private double stopOffset;// 0.01mV

    public int getUsedChnNum() {
	return usedChnNum;
    }

    public void setUsedChnNum(int usedChnNum) {
	this.usedChnNum = usedChnNum;
    }

    public double getAnodeVoltageUpLimit() {
	return anodeVoltageUpLimit;
    }

    public void setAnodeVoltageUpLimit(double anodeVoltageUpLimit) {
	this.anodeVoltageUpLimit = anodeVoltageUpLimit;
    }

    public double getAnodeVoltageDownLimit() {
	return anodeVoltageDownLimit;
    }

    public void setAnodeVoltageDownLimit(double anodeVoltageDownLimit) {
	this.anodeVoltageDownLimit = anodeVoltageDownLimit;
    }

    public double getCathodeVoltageUpLimit() {
	return cathodeVoltageUpLimit;
    }

    public void setCathodeVoltageUpLimit(double cathodeVoltageUpLimit) {
	this.cathodeVoltageUpLimit = cathodeVoltageUpLimit;
    }

    public double getCathodeVoltageDownLimit() {
	return cathodeVoltageDownLimit;
    }

    public void setCathodeVoltageDownLimit(double cathodeVoltageDownLimit) {
	this.cathodeVoltageDownLimit = cathodeVoltageDownLimit;
    }

    public double getStopOffset() {
	return stopOffset;
    }

    public void setStopOffset(double stopOffset) {
	this.stopOffset = stopOffset;
    }

    @Override
    public void encode() {
	data.add((byte) usedChnNum);
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (anodeVoltageUpLimit * 100), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (anodeVoltageDownLimit * 100), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (cathodeVoltageUpLimit * 100), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (cathodeVoltageDownLimit * 100), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stopOffset * 100), 4, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	usedChnNum = data.get(index++);
	anodeVoltageUpLimit = ProtocolUtil.compose(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
	anodeVoltageDownLimit = ProtocolUtil.compose(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
	cathodeVoltageUpLimit = ProtocolUtil.compose(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
	cathodeVoltageDownLimit = ProtocolUtil.compose(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
	stopOffset = ProtocolUtil.compose(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
    }

    @Override
    public Code getCode() {
	return VolCode.ALERT_VALUE_SET_CODE;
    }

    @Override
    public String toString() {
	return "VBoardAlertValueSetData [usedChnNum=" + usedChnNum + ", anodeVoltageUpLimit=" + anodeVoltageUpLimit + ", anodeVoltageDownLimit=" + anodeVoltageDownLimit + ", cathodeVoltageUpLimit=" + cathodeVoltageUpLimit + ", cathodeVoltageDownLimit=" + cathodeVoltageDownLimit + ", stopOffset=" + stopOffset + "]";
    }

}
