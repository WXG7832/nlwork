package com.nltecklib.protocol.power.calBox.calSoft.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.WorkState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class Channel extends Data {

    public WorkState workState = WorkState.NONE;
    public Date startTime;
    public Date endTime;

    // 显示信息
    public Pole pole;
    public CalMode step;
    public double da;
    public double meterVal;
    public double adc;
    public int currentTestDotIdx;
    public int testPlanSize;
    public int currentRange;
    public int elapse;

    private boolean selected; // 通道被选中

    public byte idxInPartition; // 在分区中的序号
    public byte idxInDevice; // 在整柜中的序号
    public byte bindChnIdx = -1; // 设备通道 与 校准板通道, 互相绑定, -1=未对接
    public byte partitionIdx; // 所在分区号
    public List<TestType> testTypeLst = new LinkedList<>();// 要执行的测试类型
    public byte moduleNum; // 膜片数量
    public byte moduleFlag; // 要测试的膜片, bit表示选中
    public transient List<TestDot> testDotLst; // 当前测试点列表
    public transient Map<String, List<TestDot>> testDotLstMap = new HashMap<>(); // 各组测试点列表
    public transient Map<String, List<TestLog>> testLogLstMap = new HashMap<>(); // 各组测试日志列表
    public transient List<Double> matchAdcLst = new ArrayList<Double>(); // 对接ADC列表
    public double higherMatchADC; // 最高点对接ADC
    public double originMatchADC; // 最后的原始对接ADC

    public void setWorkState(TestType testType, boolean testResult) {
	if (testType == TestType.MODULE_CAL) {
	    if (testResult) {
		workState = WorkState.CALIBRATE_PASS;
	    } else {
		workState = WorkState.CALIBRATE_FAIL;
	    }
	} else {
	    if (testResult) {
		workState = WorkState.MEASURE_PASS;
	    } else {
		workState = WorkState.MEASURE_FAIL;
	    }
	}
    }

    public void setWorkState(TestType testType) {
	if (testType == TestType.MODULE_CAL) {
	    workState = WorkState.CALIBRATE;
	} else {
	    workState = WorkState.MEASURE;
	}
    }

    // 平均对接ADC
    public double avgMatchADC() {
	// 总和
	double sum = 0;
	for (double val : matchAdcLst) {
	    sum += val;
	}
	// 平均数
	double avgADC = sum / matchAdcLst.size();
	return avgADC;
    }

    public boolean isWorking() {
	boolean working = false;
	if (workState == WorkState.MATCHING || workState == WorkState.CALIBRATE || workState == WorkState.MEASURE) {
	    working = true;
	}
	return working;
    }

    // 取出指定测试点列表, 若不存在, 则新建
    public List<TestDot> getTestDotLst(String testName) {
	List<TestDot> testDotLst = testDotLstMap.get(testName);
	if (testDotLst == null) {
	    testDotLst = new LinkedList<TestDot>();
	    testDotLstMap.put(testName, testDotLst);
	}
	return testDotLst;
    }

    // 取出指定日志列表, 若不存在, 则新建
    public List<TestLog> getTestLogLst(String testName) {
	List<TestLog> testLogLst = testLogLstMap.get(testName);
	if (testLogLst == null) {
	    testLogLst = new LinkedList<TestLog>();
	    testLogLstMap.put(testName, testLogLst);
	}
	return testLogLst;
    }

    public boolean isSelected() {
	return selected;
    }

    public void setSelected(boolean selected) {
	this.selected = selected;
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
}
