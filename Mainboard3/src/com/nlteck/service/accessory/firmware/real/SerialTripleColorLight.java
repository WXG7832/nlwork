package com.nlteck.service.accessory.firmware.real;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.ColorLightInfo;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.accessory.firmware.AbsTripleColorLight;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.ColorLightData;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:10:20
* 类说明
*/
public class SerialTripleColorLight extends AbsTripleColorLight {

	private int communicateTimeout;
	private SerialPort port;
	
	public SerialTripleColorLight(int index) {
		super(index);
		ColorLightInfo clf = MainBoard.startupCfg.getColorLightManagerInfo().colorLights.get(index);
		this.communicateTimeout = clf.communicateTimeout;
		this.port = Context.getPortManager().getPortByName(clf.portName);
		
	}

	@Override
	public void light(LightColor color, boolean twinkle) throws AlertException {
		
		ColorLightData cld = new ColorLightData();
		cld.setDriverIndex(index);
		cld.setColor(color);
		cld.setFlag((short) (twinkle ? 0x0f0f : 0xffff));
		
		  SerialUtil.configAndRecvImmediate(ProtocolType.ACCESSORY, port, 
					new ConfigDecorator(cld), communicateTimeout, 0, 2);

	}

}
