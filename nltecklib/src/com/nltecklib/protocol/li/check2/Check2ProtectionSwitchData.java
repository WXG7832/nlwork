package com.nltecklib.protocol.li.check2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月13日 下午1:28:50
* 类说明
*/
public class Check2ProtectionSwitchData extends Data implements Configable, Queryable, Responsable {
   
	private boolean enableProtection; //是否启用超压保护
	
	
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
		data.add((byte) (enableProtection ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		enableProtection = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01 ;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.ProtectionSwitchCode;
	}

	public boolean isEnableProtection() {
		return enableProtection;
	}

	public void setEnableProtection(boolean enableProtection) {
		this.enableProtection = enableProtection;
	}

	@Override
	public String toString() {
		return "Check2ProtectionSwitchData [enableProtection=" + enableProtection + "]";
	}
	
	
	

}
