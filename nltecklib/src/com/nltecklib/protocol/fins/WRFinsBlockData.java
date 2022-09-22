package com.nltecklib.protocol.fins;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;

public class WRFinsBlockData extends Data{
	private int index;
	private boolean isOn;
	

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	@Override
	public void encode() {
		data.clear();
		area = Area.WR;
		datalength = index;
		if (orient == Orient.WRITE) {
			data.add((byte) (isOn ? 1 : 0));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		isOn = data.get(0) == 1;
	}

}
