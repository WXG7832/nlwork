package com.nltecklib.protocol.li.test.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月24日 上午11:40:15
* 类说明
*/
public class DTPowerState extends Data implements Configable, Queryable, Responsable {
    
	
	private boolean isPostivePowerOK;
	private boolean isNegtivePowerOK;
	private boolean isInverterOK;
	
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
		
		data.add((byte) driverIndex);
		data.add((byte) (isPostivePowerOK ? 0x01 : 0x00));
		data.add((byte) (isNegtivePowerOK ? 0x01 : 0x00));
		data.add((byte) (isInverterOK ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		isPostivePowerOK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		isNegtivePowerOK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
        isInverterOK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverTestCode.PowerState;
	}

	public boolean isPostivePowerOK() {
		return isPostivePowerOK;
	}

	public void setPostivePowerOK(boolean isPostivePowerOK) {
		this.isPostivePowerOK = isPostivePowerOK;
	}

	public boolean isNegtivePowerOK() {
		return isNegtivePowerOK;
	}

	public void setNegtivePowerOK(boolean isNegtivePowerOK) {
		this.isNegtivePowerOK = isNegtivePowerOK;
	}

	public boolean isInverterOK() {
		return isInverterOK;
	}

	public void setInverterOK(boolean isInverterOK) {
		this.isInverterOK = isInverterOK;
	}
	
	

}
