package com.nlteck.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class ConcurrentTestor {
     
	private static int index = 0;
	public static void main(String[] args) {
		
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				
				
				if(index++ == 2) {
					
					if(executor != null) {
						
						executor.shutdownNow();
						System.out.println("exit thread");
						return;
					}
				}
				
				System.out.println("running index = " + index);
				
			}
			
			
		},100,1000,TimeUnit.MILLISECONDS);
		

	}
	
	
	@Test
	public void testaa1() {
		
		
		MbMeasureChnData data = new MbMeasureChnData();
		data.setDriverIndex(0);
		List<ReadonlyAdcData> list = new ArrayList<>(); 
		ReadonlyAdcData adc1 = new ReadonlyAdcData();
		adc1.adcList.get(0).primitiveAdc = 500;
		adc1.adcList.get(0).finalAdc = 1000;
		adc1.adcList.get(1).primitiveAdc = 800;
		adc1.adcList.get(1).finalAdc = 2000;
		list.add(adc1);
		data.setPole(Pole.POSITIVE);
		data.setMode(CalMode.CC);
		data.setAdcDatas(list);
		
		System.out.println(data);
		
		ResponseDecorator response = new ResponseDecorator(data,true);
		
		response.encode();
	}


}
