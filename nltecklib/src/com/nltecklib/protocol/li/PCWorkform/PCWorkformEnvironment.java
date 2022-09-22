package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 上午11:37:07 主控与上位机校准协议交互环境
 */
public class PCWorkformEnvironment {

	public enum PCWorkformCode implements Code {

		BaseCfgCode(0x01), CalibratePlanCode(0x02), DelayCode(0x03), SteadyCode(0x04), RequireDataCode(0x05),
		RangeCurrentPrecisionCode(0x06), CalculatePlanCode(0x07), ModeSwitchCode(0x08), MeterConnectCode(0x09),
		DriverBindCode(0x0A), ChnSelectCode(0x0B), DataPushCode(0x0D), LogPushData(0x0E), ModuleSwitchCode(0x0F),
		LogicCalibrateDebugCode(0x10), LogicCalculateDebugCode(0x11), LogicFlashWriteCode(0x12), LogDebugCode(0x13),
		CheckFlashWriteCode(0x14), TestModeCode(0x15), CalMatchCode(0x16), CheckCalibrateDebugCode(0x17),
		CheckCalculateDebugCode(0x18), DeviceConnectCode(0x19), UploadTestDotCode(0x1A), TimeCode(0x1B),
		IpCfgCode(0x1C), CalibrateTerminalCode(0x1D), BaseInfoQueryCode(0x1E), BindCalBoardCode(0x1F),
		MatchStateCode(0x20), CalUpdateModeCode(0x21), CalUpdateFileCode(0x22), WorkformUpdateCode(0x23),
		SelfTestInfoCode(0x24), CalMathValueCode(0x25),SelfCheckCode(0x26) , HeartbeatCode(0x27), BaseInfoConfigCode(0x28),
		CalRelayControlDebugCode(0x29),CalCalibrate2DebugCode(0x30),CalCalculate2DebugCode(0x31) ,
		LogicCalibrate2DebugCode(0x32) , LogicCalculate2DebugCode(0x33) , LogicFlashWrite2Code(0x34) , ConnectDeviceCode(0x35),
		ConnectMeterCode(0x36) , ConnectCalboardCode(0x37) , SwitchMeterCode(0x38) , ReadMeterCode(0x39),
		CalResistanceDebugCode(0x3A) , CalTempControlDebugCode(0x3b) , CalTempQueryDebugCode(0x3c) , DriverAddressCode(0x3d),
		DeviceSelfCheckCode(0x3e) ,DriverModeSwitchCode(0x3f) , CalBoardTestModeCode(0x40) ,ChnMapDebugCode(0x41), 
		RelaySwitchDebugCode(0x42), RelayEx(0x43),CalRelayResistanceDebugCode(0x44)
		;

		private int code;

		private PCWorkformCode(int code) {

			this.code = code;
		}

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}

		public static PCWorkformCode valueOf(int code) {

			for (PCWorkformCode temp : PCWorkformCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}

			return null;
		}

	}

	public enum TestMode {
		StopTestAndExitCalMode, // 停止测试并退出校准模式到识别模式
		EnterCalModeAndStartCal, // 进入校准模式并校准
		EnterCalModeAndStartCheck, // 进入校准模式并计量
		ClearChnReadyFlag,// 清空所有准备通道状态为未校准
	}

	// 校准点方案
	public static class CalibratePlanMode {

		public CalMode mode;
		public int  moduleIndex; //模片序号，默认为0即校准主模片
		public int level;
		public Pole pole;
		public double pKMin;
		public double pKMax;
		public double pBMin;
		public double pBMax;
		public double adcKMin;
		public double adcKMax;
		public double adcBMin;
		public double adcBMax;
		public double checkAdcKMin;
		public double checkAdcKMax;
		public double checkAdcBmin;
		public double checkAdcBmax;
		public double mainMeter;   //在组合模片校准时，主膜片预计的实际电流值
		public boolean combine;    //true表示启用组合模式，false则不启用组合模式

		public List<CalibratePlanDot> dots = new ArrayList<>();

	}

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
	public static class CalculatePlanMode {

		public CalMode mode;
		public Pole pole;
		public boolean disabled;
		public List<Double> dots = new ArrayList<>();

	}

	/**
	 * 校准状态
	 * 
	 * @author wavy_zheng 2020年10月29日
	 *
	 */
	public enum CalState {

		NONE("无状态"), READY("准备"), CALIBRATE("校准中"), CALCULATE("计量中"), CALIBRATE_PASS("校准通过"), CALCULATE_PASS("计量通过"),
		CALIBRATE_FAIL("校准失败"), CALCULATE_FAIL("计量失败");

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
			return "RangeCurrentPrecision [level=" + level + ", precide=" + precide + ", min=" + min + ", max=" + max
					+ ", maxAdcOffset=" + maxAdcOffset + ", maxMeterOffset=" + maxMeterOffset + "]";
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

	public static class PushLog {

		public int chnIndexInLogic;

		public boolean error;
		
		public String log;

		public Date date = new Date();

		@Override
		public String toString() {
			return "PushLog [chnIndexInLogic=" + chnIndexInLogic + ", error=" + error + ", log=" + log + ", date="
					+ date + "]";
		}
		
		
	}

}
