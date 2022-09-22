package com.nlteck.service.accessory;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.li.accessory.SmogAlertData;

/**
* @author  wavy_zheng
* @version 创建时间：2020年3月5日 下午4:05:50
* 类说明
*/
public class VirtualSmogAlertManager extends SmogAlertManager {
    
	private AlertState  state = AlertState.NORMAL;
	private int tickIndex ;
	
	public VirtualSmogAlertManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);
		
	}

	@Override
	public SmogAlertData readSmogState(int driverIndex) {
		tickIndex++;
		SmogAlertData sad = new SmogAlertData();
		sad.setAlertState(state);
		sad.setDriverIndex(driverIndex);
		return sad;
	}

	@Override
	public void clearSmogState(int driverIndex) throws AlertException {
		
		this.state = AlertState.NORMAL;
		
		System.out.println("清除烟雾报警器状态");
	}

}
