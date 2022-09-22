package com.nltecklib.protocol.plc2.pief44.control.machine;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44NgData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 211;
	private boolean isNg;

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
			data.add((byte) (isNg ? 1 : 0));
		}
	}
	
	public PIEF44NgData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isNg = data.get(0) == 1;
	}

	public boolean isNg() {
		return isNg;
	}

	public void setNg(boolean isNg) {
		this.isNg = isNg;
	}

	
}
