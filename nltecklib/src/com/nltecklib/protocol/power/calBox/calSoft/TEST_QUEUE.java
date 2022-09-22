package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;
import com.nltecklib.protocol.util.ProtocolUtil;

// 给待测队列压入测试任务
public class TEST_QUEUE extends Data implements Configable, Queryable, Responsable {

    private boolean enabled = false; // 开始/停止测试
    private byte moduleFlag; // 要启停的膜片, bit表示选中
    private byte partitionNum; // 要启停的分区数量, *内部变量
    private List<Byte> chnFlagLst = new LinkedList<>(); // 要启停的通道, bit表示选中
    private byte testTypeNum; // 要执行的测试类型数量
    private List<TestType> testTypeLst = new LinkedList<>();// 要执行的测试类型

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

	data.add((byte) (enabled ? 1 : 0));
	data.add(moduleFlag);
	data.add(partitionNum);
	for (byte chnFlag : chnFlagLst) {
	    data.add(chnFlag);
	}
	data.add(testTypeNum);
	for (TestType testType : testTypeLst) {
	    data.add((byte) testType.getCode());
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {

	int index = 0;
	data = encodeData;
	enabled = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	moduleFlag = data.get(index++);
	partitionNum = data.get(index++);
	chnFlagLst.clear();
	for (byte i = 0; i < partitionNum; i++) {
	    chnFlagLst.add(data.get(index++));
	}
	testTypeNum = data.get(index++);
	testTypeLst.clear();
	for (byte i = 0; i < testTypeNum; i++) {
	    testTypeLst.add(TestType.valueOf(data.get(index++)));
	}
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.TEST_QUEUE;
    }

    public byte getModuleFlag() {
	return moduleFlag;
    }

    public void setModuleFlag(byte moduleFlag) {
	this.moduleFlag = moduleFlag;
    }

    public List<Byte> getChnFlagLst() {
	return chnFlagLst;
    }

    public void setChnFlagLst(List<Byte> chnFlagLst) {
	this.chnFlagLst = chnFlagLst;
    }

    public List<TestType> getTestTypeLst() {
	return testTypeLst;
    }

    public void setTestTypeLst(List<TestType> testTypeLst) {
	this.testTypeLst = testTypeLst;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

}
