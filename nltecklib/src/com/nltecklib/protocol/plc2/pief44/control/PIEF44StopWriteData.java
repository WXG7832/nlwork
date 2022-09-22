package com.nltecklib.protocol.plc2.pief44.control;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44StopWriteData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 260;
	private static final int DEFAULT_INDEX = 1;
	private boolean isStop;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isStop ? 1 : 0));
		}
	}
	
	public PIEF44StopWriteData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isStop = data.get(0) == 1;
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	

}
