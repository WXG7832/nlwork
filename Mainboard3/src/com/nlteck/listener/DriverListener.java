package com.nlteck.listener;

import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
/**
 * 逻辑板采集监听事件
 * @author Administrator
 *
 */
public interface DriverListener{
	  
	  /**
	   * 采集分区状态发生变化
	   * @param db
	   * @param oldState
	   * @param newState
	   */
	  void stateChanged(DriverBoard db ,PickupState oldState , PickupState newState);
	  
	  /**
	   * 检测到逻辑板通道状态发生变更
	   * @param db
	   * @param oldState
	   * @param newState
	   */
	  void chnStateChanged(DriverBoard db ,Channel chn , ChnState oldState , ChnState newState);
	
}
