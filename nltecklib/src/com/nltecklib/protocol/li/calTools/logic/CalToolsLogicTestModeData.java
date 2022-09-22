package com.nltecklib.protocol.li.calTools.logic;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsLogicTestModeData extends Data implements Configable, Queryable, Responsable {

	private boolean testMode;
	
	

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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
		data.add((byte) unitIndex);
		data.add((byte) (testMode?5:0));
	}


	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		testMode=ProtocolUtil.getUnsignedByte(data.get(index++))==5;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsLogicCode.TestModeCode;
	}

	@Override
	public String toString() {
		return "CalToolsLogicTestModeData [testMode=" + testMode + "]";
	}

}
