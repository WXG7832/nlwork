package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;


/**
* @author  wavy_zheng
* @version 创建时间：2020年8月22日 下午1:13:43
* 类说明
*/
public class HeartBeatData extends Data implements Queryable, Responsable {

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
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.HeartBeatCode;
	}

}
