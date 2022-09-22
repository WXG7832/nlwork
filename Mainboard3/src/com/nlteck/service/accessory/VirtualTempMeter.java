package com.nlteck.service.accessory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.TempQueryData;

/**
 * 虚拟温控表
 * 
 * @author Administrator
 *
 */
public class VirtualTempMeter extends TempMeter {

	private double constTemp;
	private OverTempFlag overTempFlag = OverTempFlag.NORMAL;
	private ScheduledExecutorService executor = null;

	public VirtualTempMeter(int index) {

		super(index);
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
                
				//System.out.println("temperature:" + temperature + ",tempUpper:" + tempUpper);
				if (temperature > tempUpper) {

					// 触发报警，切断加热管
					VirtualTempMeter.this.setPs(PowerState.OFF);
					// 设置标志位
					VirtualTempMeter.this.overTempFlag = OverTempFlag.ALERT;
				} else {

					    //默认室温20°
						adjustTemperature(VirtualTempMeter.this.ps == PowerState.ON ? constTemp : 20);
					
				}

			}

		}, 1, 4, TimeUnit.SECONDS);

	}

	@Override
	public void power(PowerState ps) throws AlertException {
        
		System.out.println("index == 1 ? index = " + index  + " , " +  (index == 1 ));
		
		this.ps = ps;
        if(index == 1) {
			
        	System.out.println(this.ps);
			
		}
		
		if (ps == PowerState.ON) {
			
			overTempFlag = OverTempFlag.NORMAL;
		}

	}

	@Override
	public TempQueryData readTemperatureData() throws AlertException {

		TempQueryData tqd = new TempQueryData();
		tqd.setMainTemp(temperature);
		tqd.setSubTemp(0);
		if(index == 1) {
			
			//模拟机械报警
			//overTempFlag = OverTempFlag.ALERT;
		}
		tqd.setOverTempFlag(overTempFlag);
		return tqd;
	}

	@Override
	public void writeConstTemperature(double temp) throws AlertException {

		constTemp = temp;

	}

	@Override
	public double readConstTemperature() throws AlertException {

		return constTemp;
	}

	@Override
	public void writeTempUpper(double temp) throws AlertException {

		this.tempUpper = temp;

	}

	@Override
	public double readTempUpper() throws AlertException {

		return tempUpper;
	}

	/**
	 * 调节温度
	 */
	private void adjustTemperature(double dest) {
        
		//System.out.println("dest temp = " + dest + ",temperature = " + temperature);
		if(Math.abs(temperature - dest) <= 0.6) {
			
			temperature = dest + ( new Random().nextBoolean() ?  new Random().nextDouble() / 5 : - new Random().nextDouble() / 5 );
			return;
		}
		// 每步次温度
		double step = new Random().nextDouble() / 2;
		if(temperature > dest) {
			
			step = -step;
		}
		//System.out.println("step = " + step);
		temperature += step;
		

	}

}
