package com.nltecklib.protocol.plc2.pief44.control.machine;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44HasMaterialData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 201;
	private boolean isHas;

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
			data.add((byte) (isHas ? 1 : 0));
		}
	}
	
	public PIEF44HasMaterialData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}



	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isHas = data.get(0) == 1;
	}

	public boolean isHas() {
		return isHas;
	}

	public void setHas(boolean isHas) {
		this.isHas = isHas;
	}


}
