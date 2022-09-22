package com.nltecklib.protocol.power.calBox.calSoft.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class Config extends Data {
    public boolean debug = false;
    public int port;
    public boolean chnOrderResverse = false;
    public Map<Byte, CurrentRange> currentRangeMap = new HashMap<>();
    public Map<TestType, TestPlanInfo> testPlanMap = new HashMap<>();
    public Map<String, HeartBeat> heartBeatMap = new HashMap<>();
    public Map<String, JsonData> jsonDataMap = new HashMap<>();
    public Sqlite sqlite = new Sqlite();
    public I18N i18N = new I18N();
    public AvgADC avgADC = new AvgADC();
    public Retry retry = new Retry();
    public Delay delay = new Delay();
    public Timeout timeout = new Timeout();
    public AutoMatch autoMatch = new AutoMatch();
    public Push push = new Push();
    public VirtualDebug virtualDebug = new VirtualDebug();
    public boolean fakeFinalADC = true;
    public List<FakeFinalAdc> fakeFinalAdcLst = new ArrayList<>();

    public static class Sqlite {
	public String path;
    }

    public static class I18N {
	public String language;// 语言
	public String charsetName;// 文件编码
    }

    public static class AvgADC {
	public byte queryAdcNum;
	public byte toleranceLimit;
	public byte exceptNum;
    }

    public static class Retry {
	public short delay;
	public byte limit;
    }

    // 延时参数
    public static class Delay {
	// 开关万用表之后
	public short afterMeterOnOff;
	// 读万用表之前
	public short beforeReadMeter;
	// 开关通道之后
	public short afterChannelOnOff;
	// 切换工作状态之后
	public AfterChangeState afterChangeState = new AfterChangeState();
	// 切换测试工步之后
	public Map<CalMode, AfterChangeStep> afterChangeStepMap = new HashMap<>();
	// 下发测试点之后
	public Map<CalMode, AfterSendTestDot> afterSendTestDotMap = new HashMap<>();

	public static class AfterChangeState {
	    public short work;
	    public short update;
	    public short cal;
	}

	public static class AfterChangeStep {
	    public CalMode stepType;
	    public short delay;
	}

	public static class AfterSendTestDot {
	    public CalMode stepType;
	    public short delay;
	}
    }

    public static class CurrentRange {
	public short start;
	public short end;
    }

    public static class Timeout {
	public short device;
	public short calSoft;
	public short calBoard;
    }

    public static class TestPlanInfo {
	public String filePath;
	public TestPlan testPlan;
    }

    public static class AutoMatch {
	public short delayAfterCfg = 2000;
	public short adcTolerance = 500;
	public byte adcQryNum = 1;
	public AdcType adcType = AdcType.HIGHER;

	// 用于判定是否在范围内的ADC的类型:
	// RAW: 最后请求的原始ADC,
	// AVG: 平均计算后的ADC,
	// HIGHER: 多次请求中最高点ADC
	public enum AdcType {
	    ORIGIN, AVG, HIGHER;
	}
    }

    public static class JsonData {
	public String target;
	public boolean enabled = false;
	public boolean useGzip = true;

    }

    public static class Push {
	public byte allowNotConfirmPush = 15;
	public short interval = 3000;
	public boolean logWithByteStream = true;

    }

    public static class HeartBeat {
	public String target;
	public boolean enabled = true;
	public short interval = 5000;
	public byte failLimit = 3;

    }

    public static class VirtualDebug {
	public boolean virtualCalBoard = false;
	public boolean virtualDevice = false;
	public String jsonFilePath;
    }

    public static class FakeFinalAdc {
	public CalMode stepType;
	public Pole pole;
	public byte currentRange;
	public byte reduceMultiple;
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
