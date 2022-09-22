package com.nltecklib.protocol.li.check2;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class Check2StartupData extends Data implements Configable, Responsable {
    
	private CheckWorkMode mode = CheckWorkMode.CHECK;
	
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

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)driverIndex, 2, true)));
		data.add((byte) mode.getCode());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex =  (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(CheckWorkMode.valueOf(code) == null) {
			
			throw new RuntimeException("error startup state code " + code);
		}
		mode = CheckWorkMode.valueOf(code);

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.StartupCode;
	}

	@Override
	public String toString() {
		return "Check2StartupData [mode=" + mode + "]";
	}

	public CheckWorkMode getMode() {
		return mode;
	}

	public void setMode(CheckWorkMode mode) {
		this.mode = mode;
	}


	
	

}
