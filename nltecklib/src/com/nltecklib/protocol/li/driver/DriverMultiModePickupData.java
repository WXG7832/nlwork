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
 * 驱动板温度、基准采集通道切换
 * @author admin
 */
public class DriverMultiModePickupData extends Data implements Configable, Responsable{

	private MultiMode mode;

	public enum MultiMode{
		/** 温度 */
		TEMP,
		/** 电压基准 */
		VOLT;
	}
	
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
		
		data.add((byte) driverIndex);
	    data.add((byte) mode.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
	
		int index = 0;
	    data = encodeData;
	    
	    driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(code > MultiMode.values().length - 1) {
			
			throw new RuntimeException("error Temp/Volt mode code : " + code);
		}
		mode = MultiMode.values()[code];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.MultiModePickupCode;
	}
 
	
	public MultiMode getMultiMode() {
		return mode;
	}

	public void setMultiMode(MultiMode mode) {
		this.mode = mode;
	}

}
