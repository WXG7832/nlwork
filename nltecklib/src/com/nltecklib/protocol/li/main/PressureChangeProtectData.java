package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PressureChangeProtectData  extends Data implements Configable,Queryable,Responsable{
    
	
	private boolean pressureOk; //夹具是否到位压紧
	
	@Override
	public boolean supportUnit() {

		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) (pressureOk ? 0x01 : 00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
	
		data = encodeData;
		int index = 0;
		pressureOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
	}

	@Override
	public Code getCode() {
		
		return MainCode.PressureChangeCode;
	}

	public boolean isPressureOk() {
		return pressureOk;
	}

	public void setPressureOk(boolean pressureOk) {
		this.pressureOk = pressureOk;
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
