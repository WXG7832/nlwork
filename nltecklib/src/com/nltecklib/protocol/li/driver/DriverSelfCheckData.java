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
 * ◊‘ºÏπ ’œ≤È—Ø
 * @author admin
 */
public class DriverSelfCheckData extends Data implements  Queryable, Responsable{

	private boolean CHECK_OK;//FlashºÏ≤‚ 0:’˝≥£  1£∫π ’œ

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
		data.add((byte)(CHECK_OK ? 0x00 : 0x01));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		CHECK_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
	}

	@Override
	public Code getCode() {
		return DriverCode.SelfCheckCode;
	}

	public boolean isCHECK_OK() {
		return CHECK_OK;
	}

	public void setCHECK_OK(boolean cHECK_OK) {
		this.CHECK_OK = cHECK_OK;
	}
}
