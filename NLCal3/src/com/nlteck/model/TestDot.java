package com.nlteck.model;

import java.util.Date;

import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
/**
 * 
 * @Description: xinguo_wang 添加校准属性 
 * @date 2022年4月11日
 *
 */
public class TestDot {
    private int id;
    private int testId;
    private TestType testType;
    private int unitIndex;
    private int chnIndex;
    private CalMode mode;
    private String voltMode;
    private Pole pole;
    private int precision;
    private double meterVal;
    private double programVal;
    private double adc;
    private double checkAdc;
    private double programk;
    private double programb;
    private double adck;
    private double adcb;
    private double checkAdck;
    private double checkAdcb;
    private boolean success;
    private String info;
    private Date date;
    
    //TODO 
    
    public double mainMeter;
	public boolean combine; //是否组合校准
    public double checkAdc2;
    
    private ChannelDO channelDO;
    
    public int moduleIndex;
    
    public Date time;

    public TestResult testResult;
	public enum TestResult {
		Fail, Success
	}
    
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

 	public double minCheckAdcK;
 	public double maxCheckAdcK;
 	public double minCheckAdcB;
 	public double maxCheckAdcB;
	public double checkAdcK2;
	public double checkAdcB2;
    
	public void Compare(){
		
	}
	
    public ChannelDO getChannelDO() {
		return channelDO;
	}

	public void setChannelDO(ChannelDO channelDO) {
		this.channelDO = channelDO;
	}

	public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getTestId() {
	return testId;
    }

    public void setTestId(int testId) {
	this.testId = testId;
    }

    public TestType getTestType() {
	return testType;
    }

    public void setTestType(TestType testType) {
	this.testType = testType;
    }

    public int getUnitIndex() {
	return unitIndex;
    }

    public void setUnitIndex(int unitIndex) {
	this.unitIndex = unitIndex;
    }

    public int getChnIndex() {
	return chnIndex;
    }

    public void setChnIndex(int chnIndex) {
	this.chnIndex = chnIndex;
    }

    public CalMode getMode() {
	return mode;
    }

    public void setMode(CalMode mode) {
	this.mode = mode;
    }

    public String getVoltMode() {
	return voltMode;
    }

    public void setVoltMode(String voltMode) {
	this.voltMode = voltMode;
    }

    public Pole getPole() {
	return pole;
    }

    public void setPole(Pole pole) {
	this.pole = pole;
    }

    public int getPrecision() {
	return precision;
    }

    public void setPrecision(int precision) {
	this.precision = precision;
    }

    public double getMeterVal() {
	return meterVal;
    }

    public void setMeterVal(double meterVal) {
	this.meterVal = meterVal;
    }

    public double getProgramVal() {
	return programVal;
    }

    public void setProgramVal(double programVal) {
	this.programVal = programVal;
    }

    public double getAdc() {
	return adc;
    }

    public void setAdc(double adc) {
	this.adc = adc;
    }

    public double getCheckAdc() {
	return checkAdc;
    }

    public void setCheckAdc(double checkAdc) {
	this.checkAdc = checkAdc;
    }

    public double getProgramk() {
	return programk;
    }

    public void setProgramk(double programk) {
	this.programk = programk;
    }

    public double getProgramb() {
	return programb;
    }

    public void setProgramb(double programb) {
	this.programb = programb;
    }

    public double getAdck() {
	return adck;
    }

    public void setAdck(double adck) {
	this.adck = adck;
    }

    public double getAdcb() {
	return adcb;
    }

    public void setAdcb(double adcb) {
	this.adcb = adcb;
    }

    public double getCheckAdck() {
	return checkAdck;
    }

    public void setCheckAdck(double checkAdck) {
	this.checkAdck = checkAdck;
    }

    public double getCheckAdcb() {
	return checkAdcb;
    }

    public void setCheckAdcb(double checkAdcb) {
	this.checkAdcb = checkAdcb;
    }

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(boolean success) {
	this.success = success;
    }

    public String getInfo() {
	return info;
    }

    public void setInfo(String info) {
	this.info = info;
    }

    public boolean sameTestMode(TestDot other) {
		if (other == null) {
			return false;
		}
		if( this.moduleIndex!= other.moduleIndex) {
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
		return "TestDot [testType=" + testType + ", chnIndex=" + chnIndex + ", mode=" + mode + ", pole=" + pole
				+ ", precision=" + precision + ", meterVal=" + meterVal + ", programVal=" + programVal + ", adc=" + adc
				+ ", checkAdc=" + checkAdc + ", programk=" + programk + ", programb=" + programb + ", adck=" + adck
				+ ", adcb=" + adcb + ", success=" + success + ", info=" + info + ", moduleIndex=" + moduleIndex
				+ ", testResult=" + testResult + "]";
	}
    
}
