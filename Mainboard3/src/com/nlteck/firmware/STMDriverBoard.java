package com.nlteck.firmware;

import com.rm5248.serial.SerialPort;

/**
 * STM驱动板，实际驱动板对象
 * @author Administrator
 *
 */
public class STMDriverBoard extends DriverBoard {
   
	private SerialPort port;
	
	public STMDriverBoard(int driverIndex , MainBoard mb , SerialPort port) {
		
		
		super(driverIndex , mb);
		this.port = port;
		
	}

	public SerialPort getPort() {
		return port;
	}
	
	

}
