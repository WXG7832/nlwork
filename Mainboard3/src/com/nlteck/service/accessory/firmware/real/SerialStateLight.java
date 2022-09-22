package com.nlteck.service.accessory.firmware.real;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.StateLightInfo;
import com.nlteck.service.accessory.firmware.AbsStateLight;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.ColorLightData;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.accessory.PoleLightData;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月24日 下午5:52:33
* 类说明
*/
public class SerialStateLight extends AbsStateLight {

	private int communicateTimeout;
	private SerialPort  port;
	
	
	public SerialStateLight(int index) {
		super(index);
		StateLightInfo slf = MainBoard.startupCfg.getStateLightControllerInfo().stateLights.get(index);
		this.communicateTimeout = slf.communicateTimeout;
		this.port = Context.getPortManager().getPortByName(slf.portName);
		
	}

	@Override
	public void light(LightColor color, boolean twinkle) throws AlertException {
		
	    PoleLightData  pld = new PoleLightData();
	    pld.setUnitIndex(index);
	    pld.setColorFlag((byte) color.getCode());
	    pld.setLightFlag((short) (twinkle ? 0x0f0f : 0xffff));
	    
	    System.out.println("color flag:" + pld.getColorFlag());
		
	    SerialUtil.configAndRecvImmediate(ProtocolType.ACCESSORY, port, 
					new ConfigDecorator(pld), communicateTimeout, 0, 2);
		

	}

}
