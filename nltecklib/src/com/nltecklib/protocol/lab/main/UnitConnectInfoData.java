package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月17日 上午11:43:16
* 类说明
*/
public class UnitConnectInfoData extends Data implements Alertable, Responsable {
    
	private boolean connect;
	
	
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
		
		data.add((byte) (connect ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		connect = data.get(index++) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitConnectInfoCode;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	@Override
	public String toString() {
		return "UnitConnectInfoData [connect=" + connect + "]";
	}
	
	
	

}
