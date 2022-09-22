package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年7月4日 下午1:32:34
* 新的协议DcDv包协议
*/
public class DcDvProtectData extends Data implements Configable, Queryable, Responsable , Cloneable {
   
	private DCProtectData  dc   =  new DCProtectData();
	private DVProtectData  dv   =  new DVProtectData();
	
	
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
		
		dc.encode();
		data.addAll(dc.getEncodeData());
		dv.encode();
		data.addAll(dv.getEncodeData());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		dc = new DCProtectData();
		dc.encode();
		index = dc.getEncodeData().size();
		dc.decode(data.subList(0, index));
		
		dv = new DVProtectData();
		dv.decode(data.subList(index, data.size()));
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.DcDvProtectCode;
	}


	public DCProtectData getDc() {
		return dc;
	}

	public void setDc(DCProtectData dc) {
		this.dc = dc;
	}

	public DVProtectData getDv() {
		return dv;
	}

	public void setDv(DVProtectData dv) {
		this.dv = dv;
	}

	@Override
	public String toString() {
		return "DcDvProtectData [dc=" + dc + ", dv=" + dv + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		DcDvProtectData protect =  (DcDvProtectData)super.clone();
		protect.dc = (DCProtectData) this.dc.clone();
		protect.dv = (DVProtectData) this.dv.clone();
		return protect;
	}

}
