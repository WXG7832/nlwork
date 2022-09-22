package com.nltecklib.protocol.plc2;

import com.nltecklib.protocol.fins.Data;

public abstract class PlcData extends Data{
	protected int fixtureIndex;
	protected boolean isIC;
	public int getFixtureIndex() {
		return fixtureIndex;
	}
	public void setFixtureIndex(int fixtureIndex) {
		this.fixtureIndex = fixtureIndex;
	}
	public boolean isIC() {
		return isIC;
	}
	public void setIC(boolean isIC) {
		this.isIC = isIC;
	}
	

}
