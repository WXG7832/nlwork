package com.nltecklib.protocol.plc2.pief44.control;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44AlertReadData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 260;
	private static final int DEFAULT_INDEX = 5;
	private boolean isAlert;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isAlert ? 1 : 0));
		}
	}
	
	public PIEF44AlertReadData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isAlert = data.get(0) == 1;
	}

	public boolean isAlert() {
		return isAlert;
	}

	public void setAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}

	

}
