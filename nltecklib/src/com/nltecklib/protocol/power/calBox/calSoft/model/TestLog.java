package com.nltecklib.protocol.power.calBox.calSoft.model;

import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.LogSource;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.LogType;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;

public class TestLog extends Data {

    public boolean isError;

    public LogSource logSource;

    public String log;

    public Date date = new Date();

    public byte moduleFlag;

    public LogType logType = LogType.Global;

    public List<Byte> byteStream;

    public TestType testType;

    public String testName; // 测试名(膜片号, 测试类型组成)

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
	// TODO Auto-generated method stub

    }

    @Override
    public void decode(List<Byte> encodeData) {
	// TODO Auto-generated method stub

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return null;
    }

    public void setModuleFlag(byte moduleIdx) {
	moduleFlag = (byte) (moduleFlag | 0x01 << moduleIdx);
    }
}
