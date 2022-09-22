package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;

/**
 * 辅助电源基类
 * @author Administrator
 *
 */
public abstract class AuxiliaryPower {
     
	protected int   index;
	
	protected PowerState  ps = PowerState.OFF;
	
	protected WorkState   ws = WorkState.NORMAL;
	
	protected long   runMiliseconds;
	
	
	protected AuxiliaryPower(int index) {
		
		this.index = index;
	}

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

	public int getIndex() {
		return index;
	}
	
	
	
	
	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	/**
	 * 开启或关闭辅助电源
	 * @param ps
	 * @throws AlertException
	 */
	public abstract void power(PowerState ps) throws AlertException;
	
	
	
}
