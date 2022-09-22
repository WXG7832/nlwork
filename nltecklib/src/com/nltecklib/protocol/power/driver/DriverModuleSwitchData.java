package com.nltecklib.protocol.power.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月17日 上午9:23:33
* 类说明
*/
public class DriverModuleSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private boolean  open;
	
	
	@Override
	public boolean supportDriver() {
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
		
        data.add((byte) (open ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		open = data.get(index++) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.ModuleSwitchCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public String toString() {
		return "DriverModuleSwitchData [open=" + open + "]";
	}
	
	

}
