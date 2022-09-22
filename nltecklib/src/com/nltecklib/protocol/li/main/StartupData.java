package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.util.ProtocolUtil;

public class StartupData extends Data implements Configable,Queryable,Responsable{

	private State state;


	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "UnitStateData [unitIndex=" + unitIndex + ", state=" + state + "]";
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) state.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(data.get(index) > State.values().length - 1) {
			
			throw new RuntimeException("invalid startup state:" + data.get(index)) ;
		}
		state = State.values()[data.get(index++)];
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.StartupCode;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

}
