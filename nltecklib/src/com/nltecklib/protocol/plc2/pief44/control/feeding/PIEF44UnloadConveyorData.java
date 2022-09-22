package com.nltecklib.protocol.plc2.pief44.control.feeding;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44UnloadConveyorData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 212;
	private static final int DEFAULT_INDEX = 7;
	private boolean isSign;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isSign ? 1 : 0));
		}
	}
	
	public PIEF44UnloadConveyorData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isSign = data.get(0) == 1;
	}

	public boolean isSign() {
		return isSign;
	}

	public void setSign(boolean isSign) {
		this.isSign = isSign;
	}

	

}
