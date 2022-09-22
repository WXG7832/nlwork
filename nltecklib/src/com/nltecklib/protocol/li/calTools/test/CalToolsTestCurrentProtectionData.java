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

public class CalToolsTestCurrentProtectionData extends Data implements Configable, Queryable, Responsable {

	private double posCurrent;
	private double negCurrent;

	

	public double getPosCurrent() {
		return posCurrent;
	}

	public void setPosCurrent(double posCurrent) {
		this.posCurrent = posCurrent;
	}

	public double getNegCurrent() {
		return negCurrent;
	}

	public void setNegCurrent(double negCurrent) {
		this.negCurrent = negCurrent;
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
		data.addAll(Arrays.asList(ProtocolUtil.split((long)posCurrent, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)negCurrent, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		posCurrent = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		negCurrent = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsTestCode.CurrentProtectionCode;
	}

	@Override
	public String toString() {
		return "CalToolsTestCurrentPickupData [posCurrent=" + posCurrent + ", negCurrent=" + negCurrent + "]";
	}

}
