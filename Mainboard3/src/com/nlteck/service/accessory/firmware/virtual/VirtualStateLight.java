package com.nlteck.service.accessory.firmware.virtual;

import com.nlteck.AlertException;
import com.nlteck.service.accessory.firmware.AbsStateLight;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月24日 下午6:07:09
* 类说明
*/
public class VirtualStateLight extends AbsStateLight {

	public VirtualStateLight(int index) {
		super(index);
		
	}

	@Override
	public void light(LightColor color, boolean twinkle) throws AlertException {
		
		
		System.out.println("light :" + color + ",twinkle:" + twinkle);

	}

}
