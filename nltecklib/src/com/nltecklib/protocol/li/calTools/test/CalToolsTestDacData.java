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

public class CalToolsTestDacData extends Data implements Configable, Queryable, Responsable {

	private int  dac;


	public int getDac() {
		return dac;
	}

	public void setDac(int dac) {
		this.dac = dac;
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
		data.addAll(Arrays.asList(ProtocolUtil.split(dac, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		dac = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsTestCode.DacCode;
	}

	@Override
	public String toString() {
		return "CalToolsTestDacData [da=" + dac + "]";
	}

}
