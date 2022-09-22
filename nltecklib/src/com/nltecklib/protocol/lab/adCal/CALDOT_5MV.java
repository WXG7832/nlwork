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

public class CALDOT_5MV extends Data implements Configable, Queryable, Responsable {

    private short da = 0; // 程控值
    private double ad0 = 0; // 基于ad0的adc, 在ad1的kb列表中, 定位ad1的adc
    private double ad1 = 0;

    public CALDOT_5MV() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(da, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) ad0, 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (ad1 * CalEnvironment.ADC_RESO), 4, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	da = (short) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true));
	index += 2;
	ad0 = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true));
	index += 4;
	ad1 = (double) (ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)) / CalEnvironment.ADC_RESO;
    }

    @Override
    public Code getCode() {

	return ADCalCode.CALDOT_5MV;
    }

    public short getDA() {
	return da;
    }

    public void setDA(short da) {
	this.da = da;
    }

    public double getAD0() {
	return ad0;
    }

    public void setAD0(double ad0) {
	this.ad0 = ad0;
    }

    public double getAD1() {
	return ad1;
    }

    public void setAD1(double ad1) {
	this.ad1 = ad1;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "校准点_AD1(0x05)";
    }
}
