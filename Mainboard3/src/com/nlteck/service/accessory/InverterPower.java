package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.PowerErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;

/**
 * 逆变电源组件
 * @author Administrator
 *
 */
public abstract class InverterPower {
    
	protected PowerGroup     group;
	
	protected PowerState ps = PowerState.OFF;
	
	protected WorkState  ws = WorkState.NORMAL;
	
	protected int        powerIndexInGroup;
	protected int        powerIndexInDevice;
	protected PowerFaultReasonData   fault; //国电故障详情
	protected PowerErrorInfoData     tmbFault; //图伟逆变故障详情
	
	protected long       runMiliseconds;
	
	public InverterPower(PowerGroup group , int powerIndexInDevice) {
		
		this.group = group;
		this.powerIndexInDevice = powerIndexInDevice;
	}
	
	/**
	 * 启动或关闭逆变电源
	 * @param ps
	 */
	public abstract void power(PowerState ps) throws AlertException;
    
	
	public PowerState getPs() {
		return ps;
	}

	public void setPs(PowerState ps) {
		this.ps = ps;
	}

	public WorkState getWs() {
		return ws;
	}

	public void setWs(WorkState ws) {
		
		
		this.ws = ws;
	}

	public PowerGroup getGroup() {
		return group;
	}

	public int getPowerIndexInGroup() {
		return powerIndexInGroup;
	}

	public PowerFaultReasonData getFault() {
		return fault;
	}

	public void setFault(PowerFaultReasonData fault) {
		this.fault = fault;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	public PowerErrorInfoData getTmbFault() {
		return tmbFault;
	}

	public void setTmbFault(PowerErrorInfoData tmbFault) {
		this.tmbFault = tmbFault;
	}
	
	
	
	
	
	
}
