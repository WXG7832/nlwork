package com.nltecklib.protocol.plc2.pief44.control.machine;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44Move1Data extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 202;
	private boolean isMove;

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
			data.add((byte) (isMove ? 1 : 0));
		}
	}
	
	public PIEF44Move1Data() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isMove = data.get(0) == 1;
	}

	public boolean isMove() {
		return isMove;
	}

	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}

	

	
}
