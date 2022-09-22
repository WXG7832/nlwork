package com.nltecklib.protocol.li.workform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;


public class MainHeartBeatData extends Data implements Configable, Responsable {

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

	@Override
	public void encode() {
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		

	}

	@Override
	public Code getCode() {
		
		return WorkformCode.HeartBeatCode;
	}

}
