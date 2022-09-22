package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月28日 下午7:18:27
* 类说明
*/
public class CalBoardTestModeData extends Data implements Configable, Responsable, Queryable {
    
	private TestType  testType;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		data.add((byte) testType.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex    = ProtocolUtil.getUnsignedByte(data.get(index++));
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > TestType.values().length - 1) {

			throw new RuntimeException("error TestType mode index : " + mode);
		}
		testType = TestType.values()[mode];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalBoardTestModeCode;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	@Override
	public String toString() {
		return "CalBoardTestModeData [testType=" + testType + ", driverIndex=" + driverIndex + "]";
	}
	
	

}
