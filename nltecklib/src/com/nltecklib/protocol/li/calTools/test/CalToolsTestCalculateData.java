package com.nltecklib.protocol.li.calTools.test;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestEnvironment.CalToolsTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsTestCalculateData extends Data implements Configable, Queryable, Responsable {

	private double  calculateDot;


	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(calculateDot*100), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)/100;
		index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsTestCode.CalculateCode;
	}

	@Override
	public String toString() {
		return "CalToolsTestCalculateData [calculateDot=" + calculateDot + "]";
	}
	

}
