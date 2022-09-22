package com.nlteck.service.accessory;

import java.util.concurrent.ScheduledExecutorService;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.TempQueryData;
import com.nltecklib.protocol.li.accessory.TempStateQueryData;

/**
 * ÐéÄâÎÂ¿Ø°å
 * 
 * @author Administrator
 *
 */
public class VirtualTemperatureManager extends TemperatureManager {

	private ScheduledExecutorService executor = null;

	public VirtualTemperatureManager(MainBoard mb) throws AlertException {

		super(mb);
		// ³õÊ¼»¯±í
		meter = new VirtualTempMeter(meterInfo.index);
		if (protectMeterInfo != null && protectMeterInfo.use) {
			protectedMeter = new VirtualTempMeter(protectMeterInfo.index);
		}

	}

	@Override
	public TempStateQueryData readTempState() throws AlertException {

		TempStateQueryData tsqd = new TempStateQueryData();
		byte runFlag = 0;

		if (meter.getPs() == PowerState.ON) {

			runFlag = (byte) (runFlag | 0x01);
		}
		
		if (protectedMeter != null && protectedMeter.getPs() == PowerState.ON) {

			runFlag = (byte) (runFlag | 0x02);
		}

		tsqd.setRunFlag(runFlag);

		byte faultFlag = 0;
		if (meter.getWs() == WorkState.FAULT) {

			faultFlag = (byte) (faultFlag | 0x01);
		}
		if (protectedMeter != null && protectedMeter.getWs() == WorkState.FAULT) {

			faultFlag = (byte) (faultFlag | 0x02);
		}
		tsqd.setErrFlag(faultFlag);

		return tsqd;
	}

	@Override
	public void writeTemperature(double temperature) throws AlertException {
		
		meter.writeConstTemperature(temperature);
		if (protectedMeter != null) {
			
			protectedMeter.writeConstTemperature(temperature);
		}
		constTemp = temperature;
		reset = false;
		

	}

	@Override
	public void writeTempUpper(double temperature) throws AlertException {
		
       if (protectedMeter != null) {
			
			protectedMeter.writeTempUpper(temperature);
		}
        meter.setTempUpper(temperature);
	}

	@Override
	public double readTempUpper() throws AlertException {
		
		 if(protectedMeter != null ) {  
				
			 return protectedMeter.readTempUpper();
		 }else {
			 
			 return meter.getTempUpper();
		 }
	}

	@Override
	public void writeTempLower(double temperature) throws AlertException {
		
         meter.setTempLower(temperature);
	}

	@Override
	public double readTempLower() throws AlertException {
		
		return  meter.getTempLower();
	}

	@Override
	public void power(PowerState ps) throws AlertException {
		
		   meter.power(ps);
		   if(protectedMeter != null ) {   
			   protectedMeter.power(ps);
		   }
		   reset = false;
		   elapsedSeconds = 0;

	}

	@Override
	protected TempQueryData readTempQueryData() throws AlertException {

		TempQueryData tqd =  meter.readTemperatureData();
		meter.setTemperature(tqd.getMainTemp());
		meter.setOverFlag(tqd.getOverTempFlag());
		
		if(protectedMeter != null) {
			
			//¶ÁÈ¡¸¨Öú±íÎÂ¶È
			TempQueryData tqd2 = protectedMeter.readTemperatureData();
			protectedMeter.setTemperature(tqd2.getMainTemp());
			protectedMeter.setOverFlag(tqd2.getOverTempFlag());
		}
		return tqd;
		
	}

	@Override
	public void writeHeatpipeState(boolean open) throws AlertException {
		// TODO Auto-generated method stub
		
	}

}
