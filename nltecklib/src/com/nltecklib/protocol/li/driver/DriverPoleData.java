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
 * 极性反转
 * @author admin
 */
public class DriverPoleData extends Data implements Configable, Queryable, Responsable {

	private boolean  POLE_POS; //极性:正极
	
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
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte)driverIndex);
		
   	    data.add((byte)(POLE_POS ? 0x01:0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
	    data = encodeData;
	    
	    driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    
	    POLE_POS = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	    
	}

	@Override
	public Code getCode() {
		return DriverCode.PoleCode;
	}
	

	public boolean isPOLE_POS() {
		return POLE_POS;
	}

	public void setPOLE_POS(boolean POLE_POS) {
		this.POLE_POS = POLE_POS;
	}

}
