package com.nlteck.fireware;

import java.util.ArrayList;
import java.util.List;

import com.rm5248.serial.SerialPort;


public abstract class RelayBoard {

	protected CalibrateCore core;
	protected boolean disabled;
	
	public RelayBoard(CalibrateCore core,SerialPort serialPort) {
		this.core = core;
	}
	
	
	public abstract void cfgCalRelaySwitch(int boardIndex,int chnIndex);
	
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	
}
