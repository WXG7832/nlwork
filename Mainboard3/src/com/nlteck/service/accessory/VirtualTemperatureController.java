package com.nlteck.service.accessory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 虚拟温控器 模拟外界的温度升降，默认室温为20℃
 * 
 * @author Administrator
 *
 */
public  class VirtualTemperatureController {

	private final static double ENVIRONMENT_TEMP = 20;
	private final static double UPPER_TEMP = 95; // 温度上限
	
	private double envTemperature = ENVIRONMENT_TEMP;

	private double mainTemp = ENVIRONMENT_TEMP;
	private double subTemp = ENVIRONMENT_TEMP;
	
	private boolean heat; //是否加热模式

	public VirtualTemperatureController(double envTemp) {
            
		this.envTemperature = envTemp;
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				
				if(heat) {
					
					heat();
				}else {
					
					cool();
				}
				
			}
			
			
		}, 100, 1000, TimeUnit.MILLISECONDS);
	}
	
	public VirtualTemperatureController() {
		
		  this(ENVIRONMENT_TEMP);
	}

	public double getMainTemp() {
		return mainTemp;
	}

	public double getSubTemp() {
		return subTemp;
	}

	// 模拟加温
	private void heat() {

		if (mainTemp <= UPPER_TEMP) {
			
			mainTemp += new Random().nextDouble() / 2;
			subTemp = mainTemp - new Random().nextDouble() / 5;
		}

	}

	// 模拟降温
	private void cool() {
        
		if(mainTemp > envTemperature) {
			
			mainTemp -= new Random().nextDouble();
			subTemp = mainTemp - new Random().nextDouble() / 5;
		}
	}

	public double getEnvTemperature() {
		return envTemperature;
	}

	public void setEnvTemperature(double envTemperature) {
		this.envTemperature = envTemperature;
	}

	public boolean isHeat() {
		return heat;
	}

	public void setHeat(boolean heat) {
		this.heat = heat;
	}
	
	
	
	
}
