package com.nlteck.fireware;

import com.rm5248.serial.SerialPort;

public class VirtualRelayBoard extends RelayBoard{

	public VirtualRelayBoard(CalibrateCore core,SerialPort serialPort) {
		super(core,serialPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void cfgCalRelaySwitch(int blockNums, int chnIndex) {
		System.err.println("=========校准板号======="+blockNums+"=======通道号====="+chnIndex);
		
	}
}
