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

public class CCVProtectData extends Data implements Configable, Queryable, Responsable, Cloneable{
	
	private CCProtectData cc = new CCProtectData();
	private CVProtectData cv = new CVProtectData();

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
		// TODO Auto-generated method stub
		
		cc.encode();
		data.addAll(cc.getEncodeData());
		cv.encode();
		data.addAll(cv.getEncodeData());

		
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		
		cc.encode();
		index = cc.getEncodeData().size();
		cc.decode(data.subList(0, index));
		
		cv.decode(data.subList(index, getEncodeData().size()));
		
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CCCVProtectCode;
	}
	
	
	public CCProtectData getCcProtect() {
		return cc;
	}
	public void setCcProtect(CCProtectData cc) {
		this.cc = cc;
	}
	public CVProtectData getCvProtct() {
		return cv;
	}
	public void setCvProtect(CVProtectData cv) {
		this.cv = cv;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "CCVProtectData [ccProtectData = " + cc + ", cvProtectData = " + cv + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {

		CCVProtectData obj =  (CCVProtectData) super.clone();
		obj.cc = (CCProtectData) this.cc.clone();
		obj.cv = (CVProtectData) this.cv.clone();
		return obj;
	}
	

}
