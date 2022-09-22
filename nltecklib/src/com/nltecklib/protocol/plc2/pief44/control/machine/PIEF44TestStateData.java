package com.nltecklib.protocol.plc2.pief44.control.machine;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44TestStateData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 205;
	private boolean isOn;

	@Override
	public void encode() {
//		if (isIC) {
//		datalength = fixtureIndex;
//	}else {
//		datalength = fixtureIndex + 4;
//	}
	datalength = fixtureIndex;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isOn ? 1 : 0));
		}
	}
	
	public PIEF44TestStateData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isOn = data.get(0) == 1;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
	

}
