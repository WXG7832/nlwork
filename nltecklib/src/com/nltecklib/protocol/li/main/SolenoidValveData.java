package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class SolenoidValveData extends Data implements Configable,Queryable,Responsable{

	private SwitchState state = SwitchState.OPEN;

	@Override
	public void encode() {

		data.add((byte) state.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > SwitchState.values().length) {

			throw new RuntimeException("error valve state code : " + code);
		}
		state = SwitchState.values()[code];
	}

	@Override
	public Code getCode() {
		return MainCode.SolenoidValveSwitchCode;
	}

	public SwitchState getState() {
		return state;
	}

	public void setState(SwitchState state) {
		this.state = state;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

}
