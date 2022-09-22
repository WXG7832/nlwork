package com.nltecklib.protocol.fuel.voltage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.ChnSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板计量通道协议数据
 * 
 * @author caichao_tang
 *
 */
public class VBoardMeasureChannelData extends Data implements BoardNoSupportable, ChnSupportable, Queryable, Configable, Responsable {
    private double adcK;
    private double adcB;
    private int pickupNum;
    private ArrayList<Double> originalAdcValue = new ArrayList<Double>();
    private ArrayList<Double> finalAdcValue = new ArrayList<Double>();

    public double getAdcK() {
	return adcK;
    }

    public void setAdcK(double adcK) {
	this.adcK = adcK;
    }

    public double getAdcB() {
	return adcB;
    }

    public void setAdcB(double adcB) {
	this.adcB = adcB;
    }

    public int getPickupNum() {
	return pickupNum;
    }

    public void setPickupNum(int pickupNum) {
	this.pickupNum = pickupNum;
    }

    public ArrayList<Double> getOriginalAdcValue() {
	return originalAdcValue;
    }

    public void setOriginalAdcValue(ArrayList<Double> originalAdcValue) {
	this.originalAdcValue = originalAdcValue;
    }

    public ArrayList<Double> getFinalAdcValue() {
	return finalAdcValue;
    }

    public void setFinalAdcValue(ArrayList<Double> finalAdcValue) {
	this.finalAdcValue = finalAdcValue;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcK * (10 ^ 7)), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcB * (10 ^ 7)), 4, true)));
	data.add((byte) originalAdcValue.size());
	for (Double doubleValue : originalAdcValue) {
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (doubleValue * 100), 3, true)));
	}
	for (Double doubleValue : finalAdcValue) {
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (doubleValue * 100), 4, true)));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	adcK = ProtocolUtil.composeSpecialMinus(encodeData.subList(0, 4).toArray(new Byte[0]), true) / (10 ^ 7);
	adcB = ProtocolUtil.composeSpecialMinus(encodeData.subList(4, 8).toArray(new Byte[0]), true) / (10 ^ 7);
	pickupNum = (int) (ProtocolUtil.compose(new Byte[] { encodeData.get(8) }, true));

	originalAdcValue = new ArrayList<Double>();
	finalAdcValue = new ArrayList<Double>();

	for (int i = 9; i < encodeData.size(); i = (i + 7)) {
	    Byte[] bytes = encodeData.subList(i, i + 3).toArray(new Byte[0]);
	    double v1 = ProtocolUtil.composeSpecialMinus(bytes, true) / 100.0;
	    Byte[] bytes2 = encodeData.subList(i + 3, i + 7).toArray(new Byte[0]);
	    double v2 = ProtocolUtil.composeSpecialMinus(bytes2, true) / 100.0;

	    originalAdcValue.add(v1);
	    finalAdcValue.add(v2);
	}

    }

    @Override
    public Code getCode() {
	return VolCode.MEASURE_CHANNEL_CODE;
    }

}
