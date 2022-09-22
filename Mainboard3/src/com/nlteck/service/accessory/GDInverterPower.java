package com.nlteck.service.accessory;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nlteck.i18n.I18N;
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
 * 国电逆变电源
 * @author Administrator
 *
 */
public class GDInverterPower extends InverterPower {
    
	private SerialPort serialPort;
	private int        timeOut = 3000; //超时时间
	
	public GDInverterPower(PowerGroup group ,int powerIndexInGroup , int powerIndexInDevice, SerialPort serialPort) {
		
		this(group , powerIndexInGroup ,powerIndexInDevice  ,serialPort,3000);
	}
	
    public GDInverterPower(PowerGroup group ,int powerIndexInGroup , int powerIndexInDevice, SerialPort serialPort , int timeOut) {
		
    	super(group,powerIndexInDevice);
		this.powerIndexInGroup = powerIndexInGroup;
		this.serialPort = serialPort;
		this.timeOut = timeOut;
	}
	
	@Override
	public synchronized void power(PowerState ps) throws AlertException {
		
		PowerSwitchData cpd = new PowerSwitchData();
		cpd.setUnitIndex(powerIndexInDevice);
		cpd.setPowerType(PowerType.CHARGE);
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
				throw new AlertException(AlertCode.COMM_ERROR, "打开逆变电源通信超时");
			}
			

		}
		
		this.ps  =   ps; 
	}
	
	

}
