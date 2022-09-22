package com.nlteck.produce;

import com.nltecklib.protocol.li.logic.LogicPickupData;

/**
 * 采集或产生单板数据
 * @author Administrator
 *
 */
public interface LogicDriverDataProducer {
       
	/**
	 * 采集逻辑板数据
	 * @return
	 */
	  LogicPickupData  pickup();
	  
	  /**
	   * 更新当前逻辑板工作模式
	   */
	 // void updateProduceMode();
	
}
