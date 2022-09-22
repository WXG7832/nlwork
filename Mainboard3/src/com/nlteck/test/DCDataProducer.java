package com.nlteck.test;

import java.util.Random;

public class DCDataProducer extends RandomChnDataProducer {

	private double endVoltage; // 结束电压
	private double stepCurrent = 1.5; // 单步电压上升阀值

	private double pauseCurrent;
	private int index = 0;
	private boolean special;

	public DCDataProducer(double baseVoltage, double endVoltage, double current) {

		this.baseVoltage = baseVoltage;
		this.endVoltage = endVoltage;
		this.baseCurrent = current;
		this.stepCurrent = current / 800;
		waveVoltage = stepCurrent / 5;
		
		Random random = new Random();
		special = random.nextBoolean();
	}

	@Override
	public void produceData() {

		if (produceVoltage == 0) {

			produceVoltage = baseVoltage;
		}
		if (index < 0) {

			produceVoltage = produceRandomData(produceVoltage, produceVoltage - waveVoltage,
					produceVoltage + waveVoltage);
			produceCurrent = 0;

		} else {

			if (!close) {

				if (produceCurrent == 0) {
					// 恢复运行的模拟电流
					produceCurrent = pauseCurrent;
				}
				//System.out.println("produceRandomData , produceVoltage = " + produceVoltage + ",stepCurrent = " + stepCurrent + ",waveVoltage = " + waveVoltage);
				// 电压增量
				produceVoltage = produceRandomData(produceVoltage, produceVoltage - stepCurrent - waveVoltage,
						produceVoltage - stepCurrent + waveVoltage);
				
				produceCurrent = produceRandomData(baseCurrent, baseCurrent - waveCurrent, baseCurrent + waveCurrent);
				
				if (endVoltage > 0 && produceVoltage <= endVoltage) {
                    
					alertCurrent = produceCurrent;
					alertVoltage = produceVoltage;
					close = true;
				}
			} else {
				
				// 通道被关闭
				produceVoltage = produceRandomData(produceVoltage, produceVoltage - waveVoltage,
						produceVoltage + waveVoltage);
				produceCurrent = 0;
			}
		}
		index++;

	}

	@Override
	public void setProgram(double v, double i, double end) {
		// TODO Auto-generated method stub
		this.baseCurrent = i;
		this.baseVoltage = v;
		this.endVoltage = end;
	}

	@Override
	public void setProduceVoltage(double produceVoltage) {
		
		this.produceVoltage = produceVoltage;
		
	}

}
