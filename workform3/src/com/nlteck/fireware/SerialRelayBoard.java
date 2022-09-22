package com.nlteck.fireware;

import com.nlteck.connector.RelayBoardConnector;
import com.rm5248.serial.SerialPort;

public class SerialRelayBoard extends RelayBoard{

	private RelayBoardConnector relayBoardConnector;
	public SerialRelayBoard(CalibrateCore core, SerialPort serialPort) {
		super(core,serialPort);
		System.out.println("=====isconnect===== "+serialPort.getPortName());
		relayBoardConnector = new RelayBoardConnector(core, serialPort);
		System.out.println(relayBoardConnector.toString());
	}
	@Override
	public void cfgCalRelaySwitch(int boardIndex,int chnIndex) {
		// TODO Auto-generated method stub
		
		relayBoardConnector.cfgCalRelay(boardIndex, chnIndex);
		
	}


	
}
