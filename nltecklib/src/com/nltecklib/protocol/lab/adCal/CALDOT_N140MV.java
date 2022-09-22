package com.nltecklib.protocol.lab.adCal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CALDOT_N140MV extends Data implements Configable, Queryable, Responsable {

    private short da = 0; // 程控值
    private double adc = 0;

    public CALDOT_N140MV() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(da, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adc * CalEnvironment.ADC_RESO), 4, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	da = (short) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true));
	index += 2;
	adc = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
    }

    @Override
    public Code getCode() {

	return ADCalCode.CALDOT_N140MV;
    }

    public short getDA() {
	return da;
    }

    public void setDA(short da) {
	this.da = da;
    }

    public double getADC() {
	return adc;
    }

    public void setADC(double adc) {
	this.adc = adc;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "校准点_AD0负极性(0x04)";
    }

}
