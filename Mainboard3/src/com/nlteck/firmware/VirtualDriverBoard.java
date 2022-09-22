package com.nlteck.firmware;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.service.accessory.FanManager.FanSwitchListener;
import com.nlteck.service.accessory.VirtualTemperatureController;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.logic.LogicPickupData;

/**
 * 虚拟驱动板
 * 
 * @author Administrator
 *
 */
public class VirtualDriverBoard extends DriverBoard {

	private VirtualTemperatureController controller;

	public VirtualDriverBoard(int driverIndex, MainBoard mainboard) {
		super(driverIndex, mainboard);

		controller = new VirtualTemperatureController();

		if (Context.getAccessoriesService().getFanManager() != null) {
			Context.getAccessoriesService().getFanManager().addListener(new FanSwitchListener() {

				@Override
				public void power(boolean open) {

					// 风机打开则模拟散热
					controller.setHeat(open ? false : true);

				}

			});
		}

	}

	

	

}
