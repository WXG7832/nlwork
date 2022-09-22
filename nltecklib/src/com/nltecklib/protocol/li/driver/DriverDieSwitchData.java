package com.nltecklib.protocol.li.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 模片开关
 * @author admin
 */
public class DriverDieSwitchData extends Data implements Configable, Queryable, Responsable{

	private boolean OPEN;//使能开关
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

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
		
        data.add((byte)driverIndex);
		
		data.add((byte)chnIndex);
		
   	    data.add((byte)(OPEN ? 0x01:0x00));
	}

	@Override
	public void decode(List<Byte> data) {
		
		int index = 0;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		OPEN = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	}

	@Override
	public Code getCode() {
		return DriverCode.DieSwitchCode;
	}

	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean OPEN) {
		this.OPEN = OPEN;
	}

}
