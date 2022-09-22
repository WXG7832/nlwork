package com.nlteck.test;

import java.util.Random;

import com.nlteck.produce.ChnDataProducer;

/**
 * 模拟产生随机数据
 * @author Administrator
 *
 */
public abstract class RandomChnDataProducer implements ChnDataProducer{
     
	protected boolean  close = true;  //通道是否关闭
	protected double   waveVoltage = 0.5; //电压允许波动范围
	protected double   waveCurrent = 0.1;  //电流允许波动范围

	protected double  baseVoltage; //基准电压
	protected double  baseCurrent; //基准电流
	
	protected double produceVoltage;
	protected double produceCurrent;
	protected double alertVoltage;
	protected double alertCurrent;
	
	
	protected Random  random = new Random() ;
	
	public boolean isClose() {
		return close;
	}
	public void setClose(boolean close) {
		this.close = close;
		
	}
	public double getWaveVoltage() {
		return waveVoltage;
	}
	public void setWaveVoltage(double waveVoltage) {
		this.waveVoltage = waveVoltage;
	}
	public double getWaveCurrent() {
		return waveCurrent;
	}
	public void setWaveCurrent(double waveCurrent) {
		this.waveCurrent = waveCurrent;
	}
	public double getProduceVoltage() {
		return produceVoltage;
	}
	public double getProduceCurrent() {
		return produceCurrent;
	}
	/**
	 * 产生模拟数据
	 */
	public abstract void produceData();
	
	
	public double getBaseVoltage() {
		return baseVoltage;
	}
	public void setBaseVoltage(double baseVoltage) {
		this.baseVoltage = baseVoltage;
	}
	public double getBaseCurrent() {
		return baseCurrent;
	}
	public void setBaseCurrent(double baseCurrent) {
		this.baseCurrent = baseCurrent;
	}
	
	
	
	public double getAlertVoltage() {
		return alertVoltage;
	}
	public void setAlertVoltage(double alertVoltage) {
		this.alertVoltage = alertVoltage;
	}
	public double getAlertCurrent() {
		return alertCurrent;
	}
	public void setAlertCurrent(double alertCurrent) {
		this.alertCurrent = alertCurrent;
	}
	public double produceRandomData(double base ,double lower , double upper) {
		
		int range = (int)(upper * 100 - lower * 100);
		if(range == 0) {
			
			return base;
		}
		return (double)random.nextInt(range) / 100 + lower;
	}
	
}
