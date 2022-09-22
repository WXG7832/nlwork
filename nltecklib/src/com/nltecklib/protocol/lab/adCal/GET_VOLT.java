package com.nltecklib.protocol.lab.adCal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class GET_VOLT extends Data implements Queryable, Responsable {

    private short voltSetCount = 0; // 返回几个电压组
    private List<List<Double>> voltSetLst = new LinkedList<List<Double>>();
    private byte chnCount = 2; // 通道数

    public GET_VOLT() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {
	voltSetCount = (short) voltSetLst.size();
	data.addAll(Arrays.asList(ProtocolUtil.split(voltSetCount, 2, true)));
	for (List<Double> voltSet : voltSetLst) {
	    for (double volt : voltSet) {
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (volt * CalEnvironment.ADC_RESO), 4, true)));
	    }

	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	voltSetCount = (short) (ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
	index += 2;
	voltSetLst.clear();
	for (short n = 0; n < voltSetCount; n++) {
	    List<Double> voltSet = new LinkedList<Double>();
	    for (byte i = 0; i < chnCount; i++) {
		double volt = (double)(ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
		index += 4;

		voltSet.add(volt);
	    }
	    voltSetLst.add(voltSet);
	}
    }

    @Override
    public Code getCode() {

	return ADCalCode.GET_VOLT;
    }

    public List<List<Double>> getVoltSetLst() {
	return voltSetLst;
    }

    public void setVoltSetLst(List<List<Double>> voltSetLst) {
	this.voltSetLst = voltSetLst;
    }

    public short getVoltSetCount() {
	return voltSetCount;
    }

    public void setVoltSetCount(short voltSetCount) {
	this.voltSetCount = voltSetCount;
    }

    public void setChnCount(byte chnCount) {
	this.chnCount = chnCount;
    }

    public byte getChnCount() {
	return chnCount;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "采集电压(0x02)";
    }

}
