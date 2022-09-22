package com.nltecklib.protocol.power.calBox.calSoft.model;

import java.util.List;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestResult;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class TestDot extends Data {

    public short index; // 序号
    public byte partIdx; // 分区号
    public byte chnIdxInDevice; // 在整柜中的通道号
    public byte chnIdxInPartition; // 在分区中的通道号
    public byte chnIdxInCalBoard; // 在校准板中的通道号
    public byte moduleIdx; // 膜片序号
    public byte moduleFlag; // 膜片开关标记
    public TestType testType; // 测试类型
    public TestResult testResult; // 测试结果
    public CalMode stepType; // 工步类型
    public Pole pole; // 极性
    public byte range; // 电流量程
    public int defaultDA; // 默认量化值
    public double meterVal; // 万用表值
    public double measDot; // 计量点
    public int measDotDA; // 计量点的量化值
    public int voltDA; // 电压量化值
    public int currentDA; // 电流量化值
    public int calDotDA; // 校准点量化值
    public byte queryAdcNum; // 查询原始ADC数量
    public boolean closeChnAfterChgDot; // 切换测试点后关闭通道
    public double daK;
    public double daB;
    public double adcK;
    public double adcB;
    public double back1AdcK;
    public double back1AdcB;
    public double back2AdcK;
    public double back2AdcB;
    public double avgOrigADC;
    public double avgFinalADC;
    public double avgBack1OrigADC;
    public double avgBack1FinalADC;
    public double avgBack2OrigADC;
    public double avgBack2FinalADC;

    public double getAbsMeterVal() {
	return Math.abs(meterVal);
    }

    public void setModuleFlag(byte moduleIdx) {
	moduleFlag = (byte) (moduleFlag | 0x01 << moduleIdx);
    }

    // 获得测试名(膜片号, 测试类型组成)
    public String getTestName() {
	String testName;
	// 膜片校准/计量
	if (testType != TestType.CHANNEL_MEAS) {
	    testName = "#" + moduleIdx + testType.toString();
	}
	// 通道计量
	else {
	    testName = testType.toString();
	}
	return testName;
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
    public void decode(List<Byte> arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void encode() {
	// TODO Auto-generated method stub

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return null;
    }

}
