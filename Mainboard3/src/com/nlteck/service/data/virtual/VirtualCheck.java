package com.nlteck.service.data.virtual;
/**
* @author  wavy_zheng
* @version 创建时间：2020年12月28日 下午8:47:20
* 类说明
*/

import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.li.check2.Check2Environment.PowerState;

import java.util.concurrent.ScheduledExecutorService;

import com.nltecklib.protocol.li.check2.Check2OverVoltProcessData;
import com.nltecklib.protocol.li.check2.Check2VoltProtectData;

public class VirtualCheck {
     
	private int  index;
	private PowerState    powerState = PowerState.POWEROFF; //是否切断逆变电源
	private CheckWorkMode workMode = CheckWorkMode.CHECK;  //当前回检模式
	private Check2VoltProtectData   protect = new Check2VoltProtectData(); //超压阀值
	private Check2OverVoltProcessData  process = new Check2OverVoltProcessData(); //超压回检反馈处理
	private ScheduledExecutorService  executor ;
	
	
	public VirtualCheck(int index) {
		
		this.index = index;
		protect.setOverThreashold(4800); //默认为4800mV超压报警阀值
		
		process.setCheckCount(3);
		process.setCheckSeconds(10);
		process.setCheckTimeSpan(2000); //默认2s的间隔轮询时间
	}
	
	public PowerState getPowerState() {
		return powerState;
	}
	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}
	public CheckWorkMode getWorkMode() {
		return workMode;
	}
	public void setWorkMode(CheckWorkMode workMode) {
		this.workMode = workMode;
	}
	public int getIndex() {
		return index;
	}

	public Check2VoltProtectData getProtect() {
		return protect;
	}

	public void setProtect(Check2VoltProtectData protect) {
		this.protect = protect;
	}

	public Check2OverVoltProcessData getProcess() {
		return process;
	}

	public void setProcess(Check2OverVoltProcessData process) {
		this.process = process;
	}

	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}
	
	
	
	
}
