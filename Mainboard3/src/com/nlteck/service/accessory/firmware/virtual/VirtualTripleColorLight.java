package com.nlteck.service.accessory.firmware.virtual;

import com.nlteck.AlertException;
import com.nlteck.service.accessory.firmware.AbsTripleColorLight;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:19:41
* 类说明
*/
public class VirtualTripleColorLight extends AbsTripleColorLight {

	public VirtualTripleColorLight(int index) {
		super(index);
		
	}

	@Override
	public void light(LightColor color, boolean twinkle) throws AlertException {
		
		System.out.println("light triple color :" + color + ",twinkle:" + twinkle);

	}

}
