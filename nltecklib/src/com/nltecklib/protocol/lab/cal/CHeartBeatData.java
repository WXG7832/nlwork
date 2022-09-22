package com.nltecklib.protocol.lab.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;

public class CHeartBeatData extends Data implements Configable, Responsable{

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.HEARTBEAT;
	}

}
