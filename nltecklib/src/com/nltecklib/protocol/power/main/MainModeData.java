package com.nltecklib.protocol.power.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 下午3:11:50
* 所有分区工作模式配置
*/
public class MainModeData extends Data implements Configable, Queryable, Responsable {
   
	private DriverMode   mode;
	
	
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
		
		data.add((byte) mode.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > DriverMode.values().length - 1) {
			
			throw  new RuntimeException("error driver mode code :" + code);
		}
		
		mode = DriverMode.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ModeCode;
	}

	public DriverMode getMode() {
		return mode;
	}

	public void setMode(DriverMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "MainModeData [mode=" + mode + "]";
	}
	
	

}
