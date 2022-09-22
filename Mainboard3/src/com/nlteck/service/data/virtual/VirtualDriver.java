package com.nlteck.service.data.virtual;

import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.logic2.Logic2ProcedureData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverStepData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


/**
* @author  wavy_zheng
* @version 创建时间：2021年7月8日 下午2:40:11
* 虚拟驱动板号
*/
public class VirtualDriver {
   
	private int            driverIndex;
	private DriverStepData   procedure;
	private boolean   normal = true; // 是否运行正常
	private DriverProtectData   protect = new DriverProtectData();  //基础保护
	private ScheduledExecutorService   executor;
	private Pole     pole = Pole.POSITIVE; //默认极性为正
	private DriverMode mode  = DriverMode.WORK; //默认在工作模式
	private List<VirtualChannel> channels = new ArrayList<>();
	private DriverInfoData  driverInfo;
	
	
	
	public VirtualDriver(int driverIndex) {
		
		this.driverIndex = driverIndex ; 
		for(int n = 0 ; n < MainBoard.startupCfg.getDriverChnCount() ; n++) {
			
			VirtualChannel channel = new VirtualChannel(this , n );
			channels.add(channel);
			
		}
		//设置默认基础保护
		protect.setChnCurrUpper(500);
		protect.setChnVoltUpper(4800);
		protect.setDeviceVoltUpper(5000);
		protect.setChnVoltLower(2000);
		
		driverInfo = new DriverInfoData();
		driverInfo.setCheckSoftVersion("0.0.2");
		driverInfo.setChnCount(8);
		driverInfo.setMaxCurrent(60000);
		driverInfo.setDriverIndex(driverIndex);
		driverInfo.setDriverSoftVersion("v0.0.1");
		driverInfo.setPickSoftVersion("v1.0.0");
		driverInfo.setTempSoftVersion("v0.0.3");
	}
	
	

	public int getDriverIndex() {
		return driverIndex;
	}
	
	
	public DriverStepData getProcedure() {
		return procedure;
	}

	public void setProcedure(DriverStepData procedure) {
		this.procedure = procedure;
	}

	
    public boolean isAnyChnRunning() {

		for(VirtualChannel chn : channels) {
			
			  if(chn.getState() == ChnState.RUNNING) {
				  
				  return true;
			  }
		}
		
		return false;
		
	}



	public ScheduledExecutorService getExecutor() {
		return executor;
	}



	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}



	public List<VirtualChannel> getChannels() {
		return channels;
	}



	public DriverProtectData getProtect() {
		return protect;
	}



	public void setProtect(DriverProtectData protect) {
		this.protect = protect;
	}



	public boolean isNormal() {
		return normal;
	}



	public void setNormal(boolean normal) {
		this.normal = normal;
	}



	public Pole getPole() {
		return pole;
	}



	public void setPole(Pole pole) {
		this.pole = pole;
	}



	public DriverInfoData getDriverInfo() {
		return driverInfo;
	}



	public DriverMode getMode() {
		return mode;
	}



	public void setMode(DriverMode mode) {
		this.mode = mode;
	}
    
    
    
	
	
}
