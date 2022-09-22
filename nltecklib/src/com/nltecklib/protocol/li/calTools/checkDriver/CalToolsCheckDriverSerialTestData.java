package com.nltecklib.protocol.li.calTools.checkDriver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.checkDriver.CalToolsCheckDriverEnvironment.CalToolsCheckDriverCode;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsCheckDriverSerialTestData extends Data implements Configable, Queryable, Responsable {

	private final static int MAX_LENGTH = 255;
	private String testInfo;

	public String getTestInfo() {
		return testInfo;
	}

	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
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
		return true;
	}

	@Override
	public void encode() {
		data.add((byte) chnIndex);

		List<Byte> infoList = ProtocolUtil.convertArrayToList(testInfo.getBytes());

		if (infoList.size() > MAX_LENGTH) {
			infoList.subList(0, MAX_LENGTH);
		}

		data.add((byte) infoList.size());
		data.addAll(infoList);
	}


	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int length = ProtocolUtil.getUnsignedByte(data.get(index++));
		testInfo=new String(ProtocolUtil.convertListToArray(data.subList(index, index+length)));
		index+=length;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsCheckDriverCode.SerialTestCode;
	}

	@Override
	public String toString() {
		return "CalToolsLogicSerialTestData [testInfo=" + testInfo + "]";
	}

}
