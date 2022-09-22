package com.nltecklib.protocol.li.driver;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年5月8日 下午6:24:20
* 类说明
*/
public class DriverVersionData extends Data implements Queryable, Responsable {
    
	private String version;
	
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
		data.addAll(ProtocolUtil.encodeString(version, "utf-8", 30));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		version = ProtocolUtil.decodeString(data.subList(index, index + 30), 0, 30, "utf-8");

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.VERSION_CODE;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "DriverVersionData [version=" + version + "]";
	}
	
	

}
