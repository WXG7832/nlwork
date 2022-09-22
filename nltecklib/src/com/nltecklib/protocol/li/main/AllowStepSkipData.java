package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月21日 上午11:05:58
* 类说明
*/
public class AllowStepSkipData extends Data implements Configable, Responsable, Queryable ,Alertable {
    
	
	private int  allowedStepIndex; //允许跳转的步次序号
	
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
		data.add((byte) allowedStepIndex);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		allowedStepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.AllowStepSkipCode;
	}

	public int getAllowedStepIndex() {
		return allowedStepIndex;
	}

	public void setAllowedStepIndex(int allowedStepIndex) {
		this.allowedStepIndex = allowedStepIndex;
	}

	@Override
	public String toString() {
		return "AllowStepSkipData [allowedStepIndex=" + allowedStepIndex + "]";
	}
	
	

}
