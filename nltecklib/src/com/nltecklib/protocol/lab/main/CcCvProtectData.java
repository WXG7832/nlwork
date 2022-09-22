package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年7月4日 上午9:49:11
* 新版cccv保护!
*/
public class CcCvProtectData extends Data implements Configable, Queryable, Responsable , Cloneable {
    
	private CCProtectData   cc =  new CCProtectData();
	private CVProtectData   cv =  new CVProtectData();
	
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		cc.encode();
		data.addAll(cc.getEncodeData());
        cv.encode();
        data.addAll(cv.getEncodeData());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		cc = new CCProtectData();
		cc.encode();
		index = cc.getEncodeData().size();
		
		cc.decode(data.subList(0, index));
		
		cv = new CVProtectData();
		cv.decode(data.subList(index, data.size()));
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CcCvProtectCode;
	}

	public CCProtectData getCc() {
		return cc;
	}

	public void setCc(CCProtectData cc) {
		this.cc = cc;
	}

	public CVProtectData getCv() {
		return cv;
	}

	public void setCv(CVProtectData cv) {
		this.cv = cv;
	}

	@Override
	public String toString() {
		return "CcCvProtectData [cc=" + cc + ", cv=" + cv + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {

		CcCvProtectData protect =  (CcCvProtectData)super.clone();
		protect.cc = (CCProtectData) this.cc.clone();
		protect.cv = (CVProtectData) this.cv.clone();
		return protect;
	}
	

}
