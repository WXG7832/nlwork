package com.nltecklib.protocol.li.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.StartupState;
import com.nltecklib.protocol.util.ProtocolUtil;


public class CheckStartupData extends Data implements Configable, Responsable {
    
	private StartupState ss = StartupState.CHECK;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) ss.getCode());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(StartupState.valuesOf( code) == null) {
			
			throw new RuntimeException("error startup state code " + code);
		}
		ss = StartupState.valuesOf(code);

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.StartupCode;
	}

	public StartupState getSs() {
		return ss;
	}

	public void setSs(StartupState ss) {
		this.ss = ss;
	}
	
	

}
