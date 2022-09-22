package com.nlteck.service.accessory;

import java.util.List;

import com.nlteck.firmware.MainBoard;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData.ChannelLightData;

/**
 * 
* @author  wavy_zheng
* @version 创建时间：2022年8月9日 上午8:54:05
* 用于通道灯虚拟测试
*/
public class VirtualChannelLightController extends AbsChannelLightController {

	public VirtualChannelLightController(MainBoard mb) {
		super(mb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeChannelLight(List<ChannelLightData> list) {
		
		for(ChannelLightData cld : list) {
			
			System.out.println(cld.getIndex() + ":" + cld.getLightState());
		}

	}

}
