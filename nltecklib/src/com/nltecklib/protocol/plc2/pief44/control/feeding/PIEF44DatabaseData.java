package com.nltecklib.protocol.plc2.pief44.control.feeding;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44DatabaseData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 212;
	private static final int DEFAULT_INDEX = 1;
	private boolean isDatabaseOK;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isDatabaseOK ? 1 : 0));
		}
	}
	
	public PIEF44DatabaseData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isDatabaseOK = data.get(0) == 1;
	}

	public boolean isDatabaseOK() {
		return isDatabaseOK;
	}

	public void setDatabaseOK(boolean isDatabaseOK) {
		this.isDatabaseOK = isDatabaseOK;
	}
	

}
