package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月28日 下午12:22:21
* 单驱动板工作模式切换,用于整机测试
*/
public class DriverModeSwitchData extends Data implements Configable, Responsable, Queryable {
   
	private CalibrateCoreWorkMode mode;
	
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
		
		data.add((byte) driverIndex); // 驱动板号
		data.add((byte) mode.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = data.get(index++);
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalibrateCoreWorkMode.values().length - 1) {
			throw new RuntimeException("CalibrateCoreWorkMode code error :" + code);
		}
		mode = CalibrateCoreWorkMode.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.DriverModeSwitchCode;
	}
	
	public CalibrateCoreWorkMode getMode() {
		return mode;
	}

	public void setMode(CalibrateCoreWorkMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "DriverModeSwitchData [mode=" + mode + ", driverIndex=" + driverIndex + "]";
	}
	
	

}
