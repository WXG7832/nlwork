package com.nlteck.service.accessory;

import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;

/**
 * 风机基类
 * @author Administrator
 *
 */
public class Fan {
    
	protected int   index;
	
	protected PowerState  ps = PowerState.OFF;
	
	protected WorkState   ws = WorkState.NORMAL;
	
	protected FanType     type = FanType.COOL;
	
	protected long        runMiliseconds;
	
	protected Fan(int index , FanType  type) {
		
		this.index = index;
		this.type  = type;
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

	public FanType getType() {
		return type;
	}
	
	

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	@Override
	public String toString() {
		return "Fan [index=" + index + ", ps=" + ps + ", ws=" + ws + ", type=" + type + ", runMiliseconds="
				+ runMiliseconds + "]";
	}

	
	
	
	
	
}
