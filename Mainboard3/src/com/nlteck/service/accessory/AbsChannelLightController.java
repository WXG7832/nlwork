package com.nlteck.service.accessory;

import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData.ChannelLightData;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月8日 下午3:30:19
* 类说明
*/
public abstract class AbsChannelLightController {
    
	protected MainBoard mainboard;
	
	protected AbsChannelLightController(MainBoard mb) {
		
		this.mainboard = mb;
	}
	
	
	/**
	 * 写入通道灯颜色
	 * @author  wavy_zheng
	 * 2022年8月8日
	 * @param list
	 */
	public abstract void writeChannelLight(List<ChannelLightData> list) throws AlertException;
	
}
