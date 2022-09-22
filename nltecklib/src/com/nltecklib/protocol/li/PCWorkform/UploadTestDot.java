package com.nltecklib.protocol.li.PCWorkform;


import java.util.Date;

import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

public class UploadTestDot {

	public enum TestType {
		Cal, Measure
	}
	
	public boolean clear;//上位机清空标志

	public int unitIndex;//分区号
	public int moduleIndex; //模片序号
	public int chnIndex;//分区内通道
	
	public TestType testType;
	public CalMode mode;// 逻辑板校准模式
	public VoltMode voltMode;// 回检板 备份电压/功率电压
	public Pole pole;
	public int precision;
	

	public int pos;//当前点数
	public int range;//总点数范围
	public int seconds;//耗时
	

	public double meterVal;

	public double programVal;// 校准时，CC DC为程控电流，CV为程控电压，计量时，为计量点

	public double adc;// 校准为原始值，计量为最终值
    
	public double adc2; //第2路逻辑板adc
	
	public double checkAdc;// 回检板adc

	public double programK;// 校准时为计算出来的值，计量时为返回的当前值
	public double programB;
	public double adcK;
	public double adcB;

	public double adcK2;
	public double adcB2;
    
	public double checkAdcK;// 回检板AdcKB值
	public double checkAdcB;

	public boolean success;
	public String info;
	@Override
	public String toString() {
		return "UploadTestDot [clear=" + clear + ", unitIndex=" + unitIndex + ", moduleIndex=" + moduleIndex
				+ ", chnIndex=" + chnIndex + ", testType=" + testType + ", mode=" + mode + ", voltMode=" + voltMode
				+ ", pole=" + pole + ", precision=" + precision + ", pos=" + pos + ", range=" + range + ", seconds="
				+ seconds + ", meterVal=" + meterVal + ", programVal=" + programVal + ", adc=" + adc + ", checkAdc="
				+ checkAdc + ", programK=" + programK + ", programB=" + programB + ", adcK=" + adcK + ", adcB=" + adcB
				+ ", checkAdcK=" + checkAdcK + ", checkAdcB=" + checkAdcB + ", success=" + success + ", info=" + info
				+ "]";
	}
	
//	public Date time;
	
	
	

	
}
