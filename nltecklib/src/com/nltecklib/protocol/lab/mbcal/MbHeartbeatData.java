package com.nltecklib.protocol.lab.mbcal;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午9:35:32
* 类说明
*/
public class MbHeartbeatData extends Data implements Queryable, Responsable {

	@Override
	public boolean supportMain() {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.HEARTBEAT;
	}

}
