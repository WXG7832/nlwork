package com.nlteck.service.accessory.firmware.virtual;

import com.nlteck.AlertException;
import com.nlteck.service.accessory.firmware.AbsBeeper;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:37:24
* 类说明
*/
public class VirtualBeeper extends AbsBeeper {

	public VirtualBeeper(int index) {
		super(index);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void beep(int seconds, boolean twinkle) throws AlertException {
		
		System.out.println("beep "+  seconds + " s,twinkle:" + twinkle);

	}

	@Override
	public void close() throws AlertException {
		// TODO Auto-generated method stub
		System.out.println("close beep");
	}

}
