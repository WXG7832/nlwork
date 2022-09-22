package com.nlteck.service.accessory.firmware.real;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.accessory.firmware.AbsBeeper;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.BeepAlertData;
import com.rm5248.serial.SerialPort;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:31:56
* 类说明
*/
public class SerialBeeper extends AbsBeeper {
     
	private SerialPort port;
	private int    communicateTimeout;
	
	public SerialBeeper(int index) {
		super(index);
		
		this.communicateTimeout = MainBoard.startupCfg.getBeepAlertManagerInfo().beepInfos.get(index).communicateTimeout;
		port = Context.getPortManager().getPortByName(MainBoard.startupCfg.getBeepAlertManagerInfo().beepInfos.get(index).portName);
	}

	@Override
	public void beep(int seconds, boolean twinkle) throws AlertException {
		
		BeepAlertData bad = new BeepAlertData();
		bad.setDriverIndex(index);
		bad.setSeconds(seconds);
		bad.setFlag((short) (twinkle ? 0x0f0f : 0xffff));
		
		 SerialUtil.configAndRecvImmediate(ProtocolType.ACCESSORY, port, 
					new ConfigDecorator(bad), communicateTimeout, 0, 2);

	}

	@Override
	public void close() throws AlertException {
		
		BeepAlertData bad = new BeepAlertData();
		bad.setDriverIndex(index);
		bad.setSeconds(0);
		bad.setFlag((short)0x0000);
		
		 SerialUtil.configAndRecvImmediate(ProtocolType.ACCESSORY, port, 
					new ConfigDecorator(bad), communicateTimeout, 0, 2);

	}

}
