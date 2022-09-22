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
import com.nltecklib.protocol.li.calTools.test.CalToolsTestEnvironment.TestPole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsTestVoltagePolarityData extends Data implements Configable, Queryable, Responsable {

	private TestPole pole;


	public TestPole getPole() {
		return pole;
	}

	public void setPole(TestPole pole) {
		this.pole = pole;
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
		data.add((byte) (pole.getCode()));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		pole =TestPole.valueOf(ProtocolUtil.getUnsignedByte(data.get(index++)));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsTestCode.VoltagePolarityCode;
	}

	@Override
	public String toString() {
		return "CalToolsTestVoltagePolarityData [pole=" + pole + "]";
	}



}
