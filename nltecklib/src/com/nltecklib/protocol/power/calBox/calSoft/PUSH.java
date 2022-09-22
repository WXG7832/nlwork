package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.power.calBox.calSoft.model.Channel;
import com.nltecklib.protocol.power.calBox.calSoft.model.TestDot;
import com.nltecklib.protocol.power.calBox.calSoft.model.TestLog;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PUSH extends Data implements Alertable, Responsable {

    public static final short LOG_BYTE_LEN = 255;
    private List<TestLog> testLogLst = new ArrayList<>();
    private List<TestDot> testDotLst = new ArrayList<>();
    private List<Channel> chnLst = new ArrayList<>();

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

	data.add((byte) testLogLst.size());
	for (int n = 0; n < testLogLst.size(); n++) {

	    TestLog logPack = (TestLog) testLogLst.get(n);
	    data.add((byte) logPack.getDriverIndex());
	    data.add((byte) logPack.getChnIndex());
	    data.add((byte) (logPack.isError ? 1 : 0));
	    data.addAll(ProtocolUtil.encodeString(logPack.log, "utf-8", LOG_BYTE_LEN));
	    data.addAll(ProtocolUtil.encodeDate(logPack.date, true));
	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	int index = 0;
	data = encodeData;
	int count = ProtocolUtil.getUnsignedByte(data.get(index++));
	for (int n = 0; n < count; n++) {

	    TestLog logPack = new TestLog();
	    logPack.setDriverIndex(data.get(index++));
	    logPack.setChnIndex(data.get(index++));
	    logPack.isError = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	    logPack.log = ProtocolUtil.decodeString(data, index, LOG_BYTE_LEN, "UTF-8");
	    index += LOG_BYTE_LEN;
	    logPack.date = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);
	    index += 6;
	    appendLog(logPack);
	}

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.PUSH;
    }

    public void appendLog(TestLog logPack) {

	testLogLst.add(logPack);
    }

    public List<TestLog> getTestLogLst() {
	return testLogLst;
    }

    public void setTestLogLst(List<TestLog> testLogLst) {
	this.testLogLst = testLogLst;
    }

    public List<TestDot> getTestDotLst() {
	return testDotLst;
    }

    public void setTestDotLst(List<TestDot> testDotLst) {
	this.testDotLst = testDotLst;
    }

    public List<Channel> getChnLst() {
	return chnLst;
    }

    public void setChnLst(List<Channel> chnLst) {
	this.chnLst = chnLst;
    }

}
