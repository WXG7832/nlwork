package com.nlteck.firmware;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

public class VirtualPowerController extends PowerController {
   
	
	private boolean mainBoardPoweroff;
	
	public VirtualPowerController() {
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {

			@Override
			public void run() {
				
				//System.out.println("stimulate power off");
				//triggerPowerOffEvent();
				
			}
			
			
		}, 60, TimeUnit.SECONDS);
	}
	
	@Override
	public void powerOff() {
		
		//模拟切断电源
		mainBoardPoweroff = true;

	}

	@Override
	public void powerOn() {
		
		poweroff = false;

	}

	@Override
	public boolean resetPower(MainBoard mainboard) {
		
		boolean ok = true;
		try {
			Context.getPowerProvider().switchLogicPower(false);

			CommonUtil.sleep(2000);

			Context.getPowerProvider().switchLogicPower(true);

		} catch (AlertException e) {

			e.printStackTrace();
			AlertException ex = new AlertException(AlertCode.LOGIC,"复位辅助电源失败:" + e.getMessage());
			Context.getPcNetworkService().pushSendQueue(ex);
			ok = false;
		}
		
		CommonUtil.sleep(6000);
		// 初始化极性和保护
		for (ControlUnit cu : mainboard.getControls()) {

			try {
				cu.writeBaseProtections();
			} catch (AlertException e) {

				e.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(e);
				ok = false;
			}
		}
		
		
		
		return ok;
	}

	@Override
	public void switchLogicPower(boolean powerOn) throws AlertException {
		
		System.out.println("ok,reset logic power:" + powerOn);
		
		if(powerOn) {
			
			//模拟将驱动板温度异常状态改成正常
			//((VirtualLogicBoardService)Context.getLogicboardService()).resetPower(false);
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					//CommonUtil.sleep(60000); //模拟60秒后发生驱动板超温
					
					//((VirtualLogicBoardService)Context.getLogicboardService()).resetPower(true);
					
				}
				
				
				
			}).start();
		}
		
	}
	
	

}
