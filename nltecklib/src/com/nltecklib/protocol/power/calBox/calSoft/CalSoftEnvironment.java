package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.power.Environment.Code;

public class CalSoftEnvironment {

    public enum CalSoftCode implements Code {

	BaseCfgCode(0x01), CalibratePlanCode(0x02), DelayCode(0x03), SteadyCode(0x04), RequireDataCode(0x05), 
	RangeCurrentPrecisionCode(0x06), CalculatePlanCode(0x07), DEVICE_STATE(0x08), CONNECT_METER(0x09), 
	DriverBindCode(0x0A), SELECT_CHN(0x0B), DataPushCode(0x0D), PUSH(0x0E), ModuleSwitchCode(0x0F), 
	LogicCalibrateDebugCode(0x10), LogicCalculateDebugCode(0x11), LogicFlashWriteCode(0x12), LogDebugCode(0x13), 
	ClearLog(0x14), TEST_QUEUE(0x15), CalMatchCode(0x16), MANUAL_MATCH(0x17), 
	CheckCalculateDebugCode(0x18), CONNECT_DEVICE(0x19), UploadTestDotCode(0x1A), TIME(0x1B), 
	IP(0x1C), CalibrateTerminalCode(0x1D), BaseInfoQueryCode(0x1E), AUTO_MATCH(0x1F), 
	MatchStateCode(0x20), CalUpdateModeCode(0x21), CalUpdateFileCode(0x22), WorkformUpdateCode(0x23), 
	SelfTestInfoCode(0x24), CalMathValueCode(0x25), SelfCheckCode(0x26), HEART_BEAT(0x27), 
	BaseInfoConfigCode(0x28), CalRelayControlDebugCode(0x29), CalCalibrate2DebugCode(0x30), 
	WORK_COMPLETE(0x31), JSON_DATA(0x32);

	private int code;

	private CalSoftCode(int code) {

	    this.code = code;
	}

	@Override
	public int getCode() {
	    // TODO Auto-generated method stub
	    return code;
	}

	public static CalSoftCode valueOf(int code) {

	    for (CalSoftCode temp : CalSoftCode.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }

	    return null;
	}

    }


    // 校准点方案
//    public static class CalibratePlanMode {
//
//	public CalMode mode;
//	public int level;
//	public Pole pole;
//	public double pKMin;
//	public double pKMax;
//	public double pBMin;
//	public double pBMax;
//	public double adcKMin;
//	public double adcKMax;
//	public double adcBMin;
//	public double adcBMax;
//	public double checkAdcKMin;
//	public double checkAdcKMax;
//	public double checkAdcBmin;
//	public double checkAdcBmax;
//
//	public List<CalibratePlanDot> dots = new ArrayList<>();
//
//    }

    public static class CalibratePlanDot {

	public long da; // DA值
	public double adcMin;
	public double adcMax;
	public double meterMin; // 万用表最小值
	public double meterMax; // 万用表最大值
    }

    /**
     * 计量方案模式
     * 
     * @author wavy_zheng 2020年10月30日
     *
     */
//    public static class CalculatePlanMode {
//
//	public CalMode mode;
//	public Pole pole;
//	public boolean disabled;
//	public List<Double> dots = new ArrayList<>();
//
//    }

    /**
     * 校准状态
     * 
     * @author wavy_zheng 2020年10月29日
     *
     */
    public enum CalState {

	NONE("无状态"), READY("准备"), CALIBRATE("校准中"), CALCULATE("计量中"), CALIBRATE_PASS("校准通过"), CALCULATE_PASS("计量通过"), CALIBRATE_FAIL("校准失败"), CALCULATE_FAIL("计量失败");

	private String describe;

	private CalState(String string) {
	    describe = string;
	}

	public String getDescribe() {
	    return describe;
	}

    }

    public static class CalculateDotData {

	public int chnIndexInLogic;
	public CalState state;
	public double value;
	public double adc;
	public double meter;
	public int seconds;
	public Date date;

    }

    public static class RangeCurrentPrecision implements Comparable<RangeCurrentPrecision> {

	public int level;
	public double precide; // 精度
	public double min;
	public double max;
	public double maxAdcOffset;
	public double maxMeterOffset;

	@Override
	public String toString() {
	    return "RangeCurrentPrecision [level=" + level + ", precide=" + precide + ", min=" + min + ", max=" + max + ", maxAdcOffset=" + maxAdcOffset + ", maxMeterOffset=" + maxMeterOffset + "]";
	}

	@Override
	public int compareTo(RangeCurrentPrecision o) {
	    // TODO Auto-generated method stub
	    return level - o.level;
	}

    }

    public static class MeterCfg {

	public int index;
	public String ip;
	public boolean disabled;

	@Override
	public String toString() {
	    return "MeterCfg [index=" + index + ", ip=" + ip + ", disabled=" + disabled + "]";
	}

    }

    /**
     * 实时推送数据包结构
     * 
     * @author wavy_zheng 2020年10月30日
     *
     */
    public static class PushData {

	public int unitIndex;
	public int chnIndex;
	public CalState calState = CalState.NONE;
	public boolean matched;// 已对接
	public int matchBoardIndex;// 对接的校准板号
	public int matchChnIndex;// 校准板上的通道号
//		public CalMode calMode = CalMode.SLEEP;
//		public Pole pole = Pole.NORMAL;
//		public int precision;
//		public double currentDotVal; // 当前校准点或计量点
//		public double currentAdc;
//		public double currentMeter;
//		public int pos;
//		public int range;
//		public int seconds;
	public Date date = new Date();

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((calState == null) ? 0 : calState.hashCode());
	    result = prime * result + chnIndex;
	    result = prime * result + ((date == null) ? 0 : date.hashCode());
	    result = prime * result + matchBoardIndex;
	    result = prime * result + matchChnIndex;
	    result = prime * result + (matched ? 1231 : 1237);
	    result = prime * result + unitIndex;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    PushData other = (PushData) obj;
	    if (calState != other.calState)
		return false;
	    if (chnIndex != other.chnIndex)
		return false;
	    if (date == null) {
		if (other.date != null)
		    return false;
	    } else if (!date.equals(other.date))
		return false;
	    if (matchBoardIndex != other.matchBoardIndex)
		return false;
	    if (matchChnIndex != other.matchChnIndex)
		return false;
	    if (matched != other.matched)
		return false;
	    if (unitIndex != other.unitIndex)
		return false;
	    return true;
	}

//		@Override
//		public String toString() {
//			return "PushData [unitIndex=" + unitIndex + ", chnIndex=" + chnIndex + ", calState=" + calState
//					+ ", matched=" + matched + ", matchBoardIndex=" + matchBoardIndex + ", matchChnIndex="
//					+ matchChnIndex + ", calMode=" + calMode + ", pole=" + pole + ", precision=" + precision
//					+ ", currentDotVal=" + currentDotVal + ", currentAdc=" + currentAdc + ", currentMeter="
//					+ currentMeter + ", pos=" + pos + ", range=" + range + ", seconds=" + seconds + ", date=" + date
//					+ "]";
//		}

    }

    public enum LogType {

	Global, Channel;

    }


    public enum TestType {

	MODULE_CAL(00), MODULE_MEAS(01), CHANNEL_MEAS(02);

	private int code;

	private TestType(int code) {

	    this.code = code;
	}

	public int getCode() {
	    // TODO Auto-generated method stub
	    return code;
	}

	public static TestType valueOf(int code) {

	    for (TestType temp : TestType.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }

	    return null;
	}

	public static TestType getEnum(String str) throws Exception {
		TestType type = null;
		for (TestType testType : TestType.values()) {
		    if (str.contains(testType.toString())) {
			type = testType;
			break;
		    } else if (testType == TestType.values()[TestType.values().length - 1]) {
			throw new Exception("字符串: " + str + "未找到对应Enum元素");
		    }
		}
		return type;
	}


	@Override
	public String toString() {

	    switch (this) {
	    case MODULE_CAL:
		return "膜片校准";
	    case MODULE_MEAS:
		return "膜片计量";
	    case CHANNEL_MEAS:
		return "通道计量";

	    }
	    return "";
	}
    }
    
    public enum TestResult {

	UDT, PASS, FAIL, TESTING;

    }

    
    public enum DeviceType {

	POWERBOX, CAPACITY;

	@Override
	public String toString() {

	    switch (this) {
	    case POWERBOX:
		// return I18N.getVal(I18N.WorkBench_DeviceType_PowerBox);
		return "电源柜";
	    case CAPACITY:
		// return I18N.getVal(I18N.WorkBench_DeviceType_CAPACITY);
		return "分容柜";

	    }
	    return "";
	}

	public static DeviceType getEnum(String str) throws Exception {
	    DeviceType type = null;
	    for (DeviceType deviceType : DeviceType.values()) {
		if (str.contains(deviceType.toString())) {
		    type = deviceType;
		    break;
		} else if (deviceType == DeviceType.values()[DeviceType.values().length - 1]) {
		    throw new Exception("字符串: " + str + "非合规的设备类型");
		}
	    }
	    return type;
	}
    }

    public enum WorkState {

	NONE("无状态"), READY("准备"), MATCHING("对接中"), MATCH_PASS("对接成功"), MATCH_FAIL("对接失败"), CALIBRATE("校准中"), MEASURE("计量中"), CALIBRATE_PASS("校准通过"), MEASURE_PASS("计量通过"), CALIBRATE_FAIL("校准失败"), MEASURE_FAIL("计量失败");

	private String describe;

	private WorkState(String string) {
	    describe = string;
	}

	public String getDescribe() {
	    return describe;
	}

    }

    public enum LogSource{
	CALBOARD("校准板"), DEVICE("设备"), CALBOX("校准箱");
	
	private String describe;

	private LogSource(String string) {
	    describe = string;
	}

	public String getDescribe() {
	    return describe;
	}
    }
}
