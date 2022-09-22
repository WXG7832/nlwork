package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;

public class VirtualAuxiliaryPower extends AuxiliaryPower {

	protected VirtualAuxiliaryPower(int index) {
		super(index);
		ps = PowerState.ON; //默认打开辅助电源
	}

	@Override
	public void power(PowerState ps) throws AlertException {
		
	     setPs(ps);

	}

}
