package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.DoorData;

/**
* @author  wavy_zheng
* @version 创建时间：2020年3月6日 下午1:26:44
* 类说明
*/
public class VirtualDoorAlertManager extends DoorAlertManager {

	public VirtualDoorAlertManager(MainBoard mb) throws AlertException {
		super(mb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DoorData readDoorData(int index) throws AlertException {
		
		DoorData dd = new DoorData();
		dd.setDriverIndex(index);
		dd.setState(ValveState.OPEN);
		return dd;
	}

}
