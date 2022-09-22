package com.nltecklib.io.serialport;

import com.nltecklib.io.NlteckIOPackage;

public interface SerialListener {
	void send(byte[] sendData);
	void receive(byte[] revData);
	void receiveData(NlteckIOPackage pack);
}
