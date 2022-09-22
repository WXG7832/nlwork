package com.nlteck.test;

public class CCVDataProducer extends RandomChnDataProducer {

	private double endVoltage;
	private double endCurrent;

	private double stepVoltage;
	private double stepCurrent;
	
	private double pausedCurrent;

	private boolean cc = true;
	private int     waveCount = 0;
	private int     index = 0;

	public CCVDataProducer(double baseVoltage, double endVoltage, double current, double endCurrent) {

		this.baseVoltage = baseVoltage;
		this.endVoltage = endVoltage;
		this.baseCurrent = current;
		this.endCurrent = endCurrent;

		stepVoltage = current / 800;
		waveVoltage = 1;

		stepCurrent = endVoltage / 100;

	}

	@Override
	public void produceData() {

		if (produceVoltage == 0) {

			produceVoltage = baseVoltage;
			pausedCurrent = baseCurrent;
		}
		if (!close) {

			if (cc && produceVoltage < endVoltage) {
				// cc阶段
				produceVoltage = produceRandomData(produceVoltage, produceVoltage + stepVoltage - waveVoltage,
						produceVoltage + stepVoltage + waveVoltage);
				produceCurrent = produceRandomData(baseCurrent, baseCurrent - waveCurrent, baseCurrent + waveCurrent);              
				
				//模拟cc转cv过充
				if(endVoltage - produceVoltage > 0 && endVoltage - produceVoltage < 3) {
					
					//System.out.println("模拟电流过充" + produceCurrent + "->" + (produceCurrent + 15));
					//produceCurrent += 15; //模拟电流突增
					//produceVoltage += 15; //模拟电压突增
					
				}
				
			} else {

				cc = false;
				index = 0;
				
				if(produceCurrent == 0) {
					//恢复运行的模拟电流
					produceCurrent = pausedCurrent;
				}

				// 充电电流渐渐减小
				double step = baseCurrent - endCurrent == 0 ? 1 : stepCurrent * (produceCurrent - endCurrent) / (baseCurrent - endCurrent);
				waveCurrent = step / 2;
				
				if(waveCurrent < 0.08) {
					
					waveCurrent = 0.08;
					step = 0.16;
				}
				
				
				

				//System.out.println("produceCurrent:" + produceCurrent + "mA,wave=" + waveCurrent);
				// cv阶段
				produceCurrent = produceRandomData(produceCurrent, produceCurrent - step - waveCurrent,
						produceCurrent - step + waveCurrent);
				produceVoltage = produceRandomData(endVoltage, endVoltage - waveVoltage,
						endVoltage + waveVoltage);
				
			   
				
				//System.out.println("after produceCurrent:" + produceCurrent + "mA,wave=" + waveCurrent);
				
				pausedCurrent = produceCurrent;

				if (produceCurrent <= endCurrent) {
                     
					alertCurrent = produceCurrent;
					alertVoltage = produceVoltage;
					close = true; // 关闭
				}
			}
			index++;

		} else {
			// 通道被关闭
			produceCurrent = 0;
			produceVoltage = produceRandomData(produceVoltage, produceVoltage - waveVoltage,
					produceVoltage + waveVoltage);
		}
		
		
	}

	public boolean isCc() {
		return cc;
	}

	public void setCc(boolean cc) {
		this.cc = cc;
	}

	@Override
	public void setProgram(double v, double i,double end) {
		
		baseVoltage = v;
		baseCurrent = i;
		this.endCurrent = end;
		
	}

	@Override
	public void setProduceVoltage(double produceVoltage) {
		
		this.produceVoltage = produceVoltage;
	}
	
	

}
