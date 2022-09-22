package com.nlteck.test;

import com.nlteck.produce.ChnDataProducer;

/**
 * 休眠或待机的数据产生器
 * @author Administrator
 *
 */
public class SleepDataProducer extends RandomChnDataProducer implements ChnDataProducer {
    
	
	public SleepDataProducer(double baseVoltage) {
		
		this.baseVoltage = baseVoltage;
		
	}
	
	@Override
	public void produceData() {
		
        this.produceCurrent = 0;
        this.produceVoltage = produceRandomData(baseVoltage,baseVoltage - waveVoltage , baseVoltage + waveVoltage);
	}

	@Override
	public void setProgram(double v, double i,double end) {
		// TODO Auto-generated method stub
		this.baseCurrent = i;
		this.baseVoltage = v;
		
	}

	@Override
	public void setProduceVoltage(double produceVoltage) {
		
		this.produceVoltage = produceVoltage;
		
	}

}
