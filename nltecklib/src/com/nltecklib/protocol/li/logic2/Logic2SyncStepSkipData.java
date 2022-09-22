package com.nltecklib.protocol.li.logic2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月16日 下午5:12:58
* 类说明
*/
public class Logic2SyncStepSkipData extends Data implements Configable, Responsable {

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

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.SyncSkipCode;
	}

}
