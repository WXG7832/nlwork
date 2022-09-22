package com.nltecklib.protocol.plc2.pief44.control.machine;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44FixtureShieldData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 207;
	private boolean isShield;

	@Override
	public void encode() {
//		if (isIC) {
//			datalength = fixtureIndex;
//		}else {
//			datalength = fixtureIndex + 4;
//		}
		datalength = fixtureIndex;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isShield ? 1 : 0));
		}
	}
	
	public PIEF44FixtureShieldData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isShield = data.get(0) == 1;
	}

	public boolean isShield() {
		return isShield;
	}

	public void setShield(boolean isShield) {
		this.isShield = isShield;
	}

	

}
