package com.nltecklib.protocol.plc2.pief44.control.feeding;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;

public class PIEF44UnloadClipBindingData extends PlcData{
	
	private static final int DEFAULT_ADDRESS = 212;
	private static final int DEFAULT_INDEX = 14;
	private boolean isBinding;

	@Override
	public void encode() {
		datalength = DEFAULT_INDEX;
		data.clear();
		if (orient == Orient.WRITE) {
			data.add((byte) (isBinding ? 1 : 0));
		}
	}
	
	public PIEF44UnloadClipBindingData() {
		area = Area.WR;
		address = DEFAULT_ADDRESS;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isBinding = data.get(0) == 1;
	}

	public boolean isBinding() {
		return isBinding;
	}

	public void setBinding(boolean isBinding) {
		this.isBinding = isBinding;
	}

	
}
