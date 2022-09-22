package com.nltecklib.protocol.li.MBWorkform;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月31日 下午12:35:49
* 心跳检测，防止设备主控掉线
*/
public class MBHeartbeatData extends Data implements Queryable, Responsable {

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
		return MBWorkformCode.HeartbeatCode;
	}

}
