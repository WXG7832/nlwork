package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 上午10:56:37 类说明
 */
public class TestModeData extends Data implements Configable, Queryable, Responsable {

	public TestMode testMode;

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
        
		data.add((byte) (testMode.ordinal()));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		testMode = TestMode.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.TestModeCode;
	}

	public TestMode getTestMode() {
		return testMode;
	}

	public void setTestMode(TestMode testMode) {
		this.testMode = testMode;
	}

	@Override
	public String toString() {
		return "TestModeData [testMode=" + testMode + "]";
	}

}
