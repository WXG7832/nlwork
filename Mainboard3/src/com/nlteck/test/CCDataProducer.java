package com.nlteck.test;

public class CCDataProducer extends RandomChnDataProducer {

	private double endVoltage; // 结束电压,结束电压 = 0 不自动关闭通道
	private double stepVolt = 1.5; // 单步电压上升阀值
	

	private int index = 0;

	public CCDataProducer(double baseVoltage, double endVoltage, double current) {

		this.baseVoltage = baseVoltage;
		this.endVoltage = endVoltage;
		this.baseCurrent = current;
		this.stepVolt = current / 800;
		waveVoltage = 1;
	}

	public double getEndVoltage() {
		return endVoltage;
	}

	public void setEndVoltage(double endVoltage) {
		this.endVoltage = endVoltage;
	}

	@Override
	public void produceData() {
        
		if (produceVoltage == 0) {

			produceVoltage = baseVoltage;
		}
		
		
		if(index < 0) {
			
			produceVoltage = produceRandomData(produceVoltage, produceVoltage - waveVoltage,
					produceVoltage + waveVoltage);
			produceCurrent = 0;
			
			
		}else {

			if (!close) {

				// 电压增量
				produceVoltage = produceRandomData(produceVoltage, produceVoltage + stepVolt - waveVoltage,
						produceVoltage + stepVolt + waveVoltage);
				produceCurrent = produceRandomData(baseCurrent, baseCurrent - waveCurrent, baseCurrent + waveCurrent);
				
				
	
				if (endVoltage > 0 && produceVoltage >= endVoltage) {

					alertCurrent = produceCurrent;
					alertVoltage = produceVoltage;
					close = true;
				}
			} else {
				
				
				// 通道被关闭
				produceCurrent = 0;
				produceVoltage = produceRandomData(produceVoltage, produceVoltage - waveVoltage,
						produceVoltage + waveVoltage);
			}
			
			
		}
		
		index++;
	}

	@Override
	public void setProgram(double v, double i , double end) {
		
		baseCurrent = i;
		baseVoltage = v;
		endVoltage = end;
	}

	@Override
	public double getAlertVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAlertCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProduceVoltage(double produceVoltage) {
		
		this.produceVoltage = produceVoltage;
		
	}

}
