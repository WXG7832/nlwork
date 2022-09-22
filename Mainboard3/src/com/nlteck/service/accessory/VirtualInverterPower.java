package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;

/**
 * ÐéÄâÄæ±äµçÔ´
 * @author Administrator
 *
 */
public class VirtualInverterPower extends InverterPower {
   
	
	
	public VirtualInverterPower(PowerGroup group ,  int indexInGroup , int indexInDevice) {
		super(group,indexInDevice);
		this.powerIndexInGroup = indexInGroup;
	}

	@Override
	public void power(PowerState ps) throws AlertException {
		
		this.ps  =   ps; 
        //System.out.println("ps change " + this.ps + "->" + ps);
	}

}
