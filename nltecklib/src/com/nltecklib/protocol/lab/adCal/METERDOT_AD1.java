package com.nltecklib.protocol.lab.adCal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADC_KB;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class METERDOT_AD1 extends Data implements Configable, Queryable, Responsable {

    private double meterDot = 0;
    private byte chnCount = 2;
    private List<ADC_KB> adc_kb_lst = new LinkedList<ADC_KB>();

    public METERDOT_AD1() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) meterDot * CalEnvironment.ADC_RESO, 4, true)));
	for (ADC_KB adc_kb : adc_kb_lst) {

	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.k * Math.pow(10, CalEnvironment.KB_EXP)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.b * Math.pow(10, CalEnvironment.KB_EXP)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.adc_orig * CalEnvironment.ADC_RESO), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.adc * CalEnvironment.ADC_RESO), 4, true)));
	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	meterDot = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
	index += 4;
	adc_kb_lst.clear();
	for (byte i = 0; i < chnCount; i++) {
	    ADC_KB adc_kb = new ADC_KB();
	    adc_kb.chnIdx = i;
	    adc_kb.k = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, CalEnvironment.KB_EXP));
	    index += 4;
	    adc_kb.b = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, CalEnvironment.KB_EXP));
	    index += 4;
	    adc_kb.adc_orig = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
	    index += 4;
	    adc_kb.adc = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
	    index += 4;
	    adc_kb_lst.add(adc_kb);
	}
    }

    @Override
    public Code getCode() {

	return ADCalCode.METERDOT_AD1;
    }

    public double getMeterDot() {
	return meterDot;
    }

    public void setMeterDot(double meterDot) {
	this.meterDot = meterDot;
    }

    public List<ADC_KB> getAdc_kb_lst() {
	return adc_kb_lst;
    }

    public void setAdc_kb_lst(List<ADC_KB> adc_kb_lst) {
	this.adc_kb_lst = adc_kb_lst;
    }

    public byte getChnCount() {
	return chnCount;
    }

    public void setChnCount(byte chnCount) {
	this.chnCount = chnCount;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "¼ÆÁ¿µã_AD1(0x06)";
    }

}
