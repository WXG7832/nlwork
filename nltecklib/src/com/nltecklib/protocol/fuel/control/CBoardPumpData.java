package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * øÿ÷∆∞Â±√…Ë÷√
 * 
 * @author caichao_tang
 *
 */
public class CBoardPumpData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    private double rate;

    public double getRate() {
	return rate;
    }

    public void setRate(double rate) {
	this.rate = rate;
    }

    @Override
    public void encode() {
	Byte[] rateData = ProtocolUtil.split((long) (rate * 10), 2, true);
	data.addAll(Arrays.asList(rateData)); // ±‡¬Î
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	rate = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return ControlCode.PUMP_CODE;
    }

    @Override
    public String toString() {
	return "FBoardVariablePmpData [rate=" + rate + ", boardNum=" + boardNum + ", componentCode=" + component + "]";
    }
}
