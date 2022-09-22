package com.nltecklib.protocol.plc2.pief44.control;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44AutoReadData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 260;
	private static final int DEFAULT_INDEX = 3;
	private boolean isAutoRun;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isAutoRun ? 1 : 0));
		}
	}
	
	public PIEF44AutoReadData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isAutoRun = data.get(0) == 1;
	}

	public boolean isAutoRun() {
		return isAutoRun;
	}

	public void setAutoRun(boolean isAutoRun) {
		this.isAutoRun = isAutoRun;
	}


}
