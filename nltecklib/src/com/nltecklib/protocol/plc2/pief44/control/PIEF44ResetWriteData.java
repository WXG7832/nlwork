package com.nltecklib.protocol.plc2.pief44.control;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44ResetWriteData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 260;
	private static final int DEFAULT_INDEX = 2;
	private boolean isReset;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isReset ? 1 : 0));
		}
	}
	
	public PIEF44ResetWriteData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isReset = data.get(0) == 1;
	}

	public boolean isReset() {
		return isReset;
	}

	public void setReset(boolean isReset) {
		this.isReset = isReset;
	}



}
