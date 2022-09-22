package com.nltecklib.protocol.li.check2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月19日 下午2:56:05
* 类说明
*/
public class Check2Heartbeat extends Data implements Configable, Responsable {

	@Override
	public boolean supportUnit() {
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
	    data = encodeData;
	    int index = 0;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.Heartbeat;
	}

}
