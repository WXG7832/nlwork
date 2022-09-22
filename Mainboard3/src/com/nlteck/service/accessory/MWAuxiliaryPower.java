package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.SerialPort;

/**
 * 明威辅助电源
 * @author Administrator
 *
 */
public class MWAuxiliaryPower extends AuxiliaryPower{
     
	private SerialPort  serialPort;
	private int         timeOut = 3000;
	
	public MWAuxiliaryPower(int index , SerialPort serialPort ) {
		
		this(index,serialPort,3000);
	}
	
	public MWAuxiliaryPower(int index , SerialPort serialPort , int timeOut) {
		
		super(index);
		this.serialPort = serialPort;
		this.timeOut    = timeOut;
	}
	
	@Override
	public synchronized void power(PowerState ps) throws AlertException {
		
		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(index);
		cpd.setPowerType(PowerType.AUXILIARY);
		cpd.setState(ps);

		try {
			SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cpd), 
					timeOut);
		} catch (IOException ex) {
            
			CommonUtil.sleep(1000);
			try {
				SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort, new ConfigDecorator(cpd), 
						timeOut);
			} catch (IOException e) {
				
				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, "启动或关闭辅助电源失败");
			}
			

		}
		
		this.ps  =   ps; 
	}
}
