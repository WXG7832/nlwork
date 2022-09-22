package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月13日 下午1:36:18
* 启用或禁用回检板的超压保护
*/
public class EnableCheckProtectionData extends Data implements Configable, Queryable, Responsable {
   
	private boolean enableCheckProtection;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

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
		
		data.add((byte) unitIndex);
		data.add((byte) (enableCheckProtection ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		enableCheckProtection = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.EnableCheckProtectionCode;
	}

	public boolean isEnableCheckProtection() {
		return enableCheckProtection;
	}

	public void setEnableCheckProtection(boolean enableCheckProtection) {
		this.enableCheckProtection = enableCheckProtection;
	}

	@Override
	public String toString() {
		return "EnableCheckProtectionData [enableCheckProtection=" + enableCheckProtection + "]";
	}
	
	

}
