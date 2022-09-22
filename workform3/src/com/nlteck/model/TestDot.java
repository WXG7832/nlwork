package com.nlteck.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;


/**
 * 校准计量点
 * 
 * @author guofang_ma
 *
 */
public class TestDot implements Cloneable {

	public Channel channel;

	public TestType testType;
	public CalMode mode;// 逻辑板校准模式
	public VoltMode voltMode;// 回检板 备份电压/功率电压
	public Pole pole;
	public int precision;
	public int moudleIndex = 0; //模片序号

	public double meterVal;

	public double programVal;// 校准时，CC DC为程控电流，CV为程控电压，计量时，为计量点

	public double adc;// 校准为原始值，计量为最终值

	public double checkAdc;// 回检板adc
	public double checkAdc2; //回检板adc2

	public double programK;// 校准时为计算出来的值，计量时为返回的当前值
	public double programB;
	public double adcK;
	public double adcB;

	public double currentProgram;// 当前程控值

	// 比较值

	public double minAdc;
	public double maxAdc;
	public double minMeter;
	public double maxMeter;

	public double minProgramK;
	public double maxProgramK;
	public double minProgramB;
	public double maxProgramB;
	public double minAdcK;
	public double maxAdcK;
	public double minAdcB;
	public double maxAdcB;

	public double checkAdcK;// 回检板AdcKB值
	public double checkAdcB;
	
	public double checkAdcK2;// 回检板2AdcKB值
	public double checkAdcB2;

	public double minCheckAdcK;
	public double maxCheckAdcK;
	public double minCheckAdcB;
	public double maxCheckAdcB;
	
	public double mainMeter;
	public boolean combine; //是否组合校准

	public TestResult testResult;
	public String info;

	public Date time;

	public enum TestResult {
		Fail, Success
	}

	@Override
	public TestDot clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (TestDot) super.clone();
	}

	public boolean sameMode(TestDot other) {

		if (other == null) {
			return false;
		}
		
		if(this.moudleIndex != other.moudleIndex) {
			
			return false;
		}

		if (!this.mode.equals(other.mode)) {
			return false;
		}

		if (!this.pole.equals(other.pole)) {
			return false;
		}

		if (this.precision != other.precision) {
			return false;
		}
		
		

		return true;
	}

	@Override
	public String toString() {
		return "TestDot [channel=" + channel + ", testType=" + testType + ", mode=" + mode + ", voltMode=" + voltMode
				+ ", pole=" + pole + ", precision=" + precision + ", meterVal=" + meterVal + ", programVal="
				+ programVal + ", adc=" + adc + ", checkAdc=" + checkAdc + ", programK=" + programK + ", programB="
				+ programB + ", adcK=" + adcK + ", adcB=" + adcB + ", currentProgram=" + currentProgram + ", minAdc="
				+ minAdc + ", maxAdc=" + maxAdc + ", minMeter=" + minMeter + ", maxMeter=" + maxMeter + ", minProgramK="
				+ minProgramK + ", maxProgramK=" + maxProgramK + ", minProgramB=" + minProgramB + ", maxProgramB="
				+ maxProgramB + ", minAdcK=" + minAdcK + ", maxAdcK=" + maxAdcK + ", minAdcB=" + minAdcB + ", maxAdcB="
				+ maxAdcB + ", checkAdcK=" + checkAdcK + ", checkAdcB=" + checkAdcB + ", minCheckAdcK=" + minCheckAdcK
				+ ", maxCheckAdcK=" + maxCheckAdcK + ", minCheckAdcB=" + minCheckAdcB + ", maxCheckAdcB=" + maxCheckAdcB
				+ ", testResult=" + testResult + ", info=" + info + ", time=" + time + "]";
	}

	/**
	 * 计算精度
	 */
	public void calculatePrecision() {

		if (testType != TestType.Measure) {
			return;
		}

		Optional<RangeCurrentPrecision> a = channel.getDeviceCore().getCore().getCalCfg().rangeCurrentPrecisionData
				.getRanges().stream().filter(x -> programVal > x.min && programVal <= x.max).findAny();

		if (a.equals(Optional.empty())) {
			
			
		}

		// 临界档位往高精度
		if (mode == CalMode.CC || mode == CalMode.DC) {
			List<RangeCurrentPrecision> ranges = channel.getDeviceCore().getCore().getCalCfg().rangeCurrentPrecisionData
					.getRanges();

			precision = ranges.get(ranges.size() - 1).level;// 默认最高精度

			for (RangeCurrentPrecision range : ranges) {
				if (programVal > range.min) {
					precision = range.level;
					break;
				}
			}
		}
	}

	public String getDescription() {
		return "TestDot [testType=" + testType + ", mode=" + mode + ", voltMode=" + voltMode + ", pole=" + pole
				+ ", precision=" + precision + ", meterVal=" + meterVal + ", programVal=" + programVal + ", adc=" + adc
				+ ", checkAdc=" + checkAdc + ", programK=" + programK + ", programB=" + programB + ", adcK=" + adcK
				+ ", adcB=" + adcB + ", currentProgram=" + currentProgram + ", minProgramK=" + minProgramK
				+ ", maxProgramK=" + maxProgramK + ", minProgramB=" + minProgramB + ", maxProgramB=" + maxProgramB
				+ ", minAdcK=" + minAdcK + ", maxAdcK=" + maxAdcK + ", minAdcB=" + minAdcB + ", maxAdcB=" + maxAdcB
				+ ", checkAdcK=" + checkAdcK + ", checkAdcB=" + checkAdcB + ", minCheckAdcK=" + minCheckAdcK
				+ ", maxCheckAdcK=" + maxCheckAdcK + ", minCheckAdcB=" + minCheckAdcB + ", maxCheckAdcB=" + maxCheckAdcB
				+ "]";
	}

}
