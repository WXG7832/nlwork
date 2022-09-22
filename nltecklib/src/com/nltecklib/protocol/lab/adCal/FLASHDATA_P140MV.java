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

public class FLASHDATA_P140MV extends Data implements Configable, Queryable, Responsable {

    private byte adc_kb_lst_count;
    private List<ADC_KB> adc_kb_lst = new LinkedList<ADC_KB>();

    public FLASHDATA_P140MV() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.add(adc_kb_lst_count);
	for (ADC_KB adc_kb : adc_kb_lst) {
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.adc * CalEnvironment.ADC_RESO), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.k * Math.pow(10, CalEnvironment.KB_EXP)), 4, true)));
	    // 因为adc是缩小10^5的结果, 所以目前显示B值, 是放大10^3之后的结果, 在发给单片机时, 应该要缩小10^3, 再放大10^8
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc_kb.b / 1000 * Math.pow(10, CalEnvironment.KB_EXP)), 4, true)));
	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	adc_kb_lst_count = data.get(index);
	index++;
	adc_kb_lst.clear();
	for (byte i = 0; i < adc_kb_lst_count; i++) {
	    ADC_KB adc_kb = new ADC_KB();
	    adc_kb.adc = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
	    index += 4;
	    adc_kb.k = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, CalEnvironment.KB_EXP));
	    index += 4;
	    adc_kb.b = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, CalEnvironment.KB_EXP));
	    index += 4;
	    adc_kb_lst.add(adc_kb);
	}
    }

    @Override
    public Code getCode() {

	return ADCalCode.FLASHDATA_P140MV;
    }

    public List<ADC_KB> getADC_KB_Lst() {
	return adc_kb_lst;
    }

    public void setADC_KB_Lst(List<ADC_KB> adc_kb_lst) {
	this.adc_kb_lst = adc_kb_lst;
    }

    public byte getADC_KB_Lst_Count() {
	return adc_kb_lst_count;
    }

    public void setADC_KB_Lst_Count(byte adc_kb_lst_count) {
	this.adc_kb_lst_count = adc_kb_lst_count;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "FLASH数据_AD0正极性(0x08)";
    }

}
