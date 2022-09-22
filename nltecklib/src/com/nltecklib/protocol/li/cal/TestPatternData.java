/**
 * 
 */
package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 测试模式    功能码 0x11  可读，可配 (配置查询需带上板地址)
* @author: JenHoard_Shaw
* @date: 2022年3月23日 下午1:21:38 
*
*/
public class TestPatternData extends Data implements Configable, Queryable, Responsable {

	private int chnnIndex; // 通道号
	private TestType testType; // 测试类型
	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);
		data.add((byte) chnnIndex);
		data.add((byte) testType.ordinal());
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		chnnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > TestType.values().length - 1) {

			throw new RuntimeException("error TestType mode index : " + mode);
		}
		testType = TestType.values()[mode];
		
	}

	@Override
	public Code getCode() {
		
		return CalEnvironment.CalCode.TestPatternCode;
	}

	public int getChnnIndex() {
		return chnnIndex;
	}

	public void setChnnIndex(int chnnIndex) {
		this.chnnIndex = chnnIndex;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

}
