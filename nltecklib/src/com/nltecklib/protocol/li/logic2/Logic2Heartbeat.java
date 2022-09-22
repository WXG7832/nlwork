package com.nltecklib.protocol.li.logic2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月19日 下午1:52:16
* 心跳
*/
public class Logic2Heartbeat extends Data implements Configable, Responsable {

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.Heartbeat;
	}

}
