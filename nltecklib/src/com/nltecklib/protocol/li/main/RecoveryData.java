package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月22日 上午8:51:21
* 对设备进行紧急恢复，将流程状态标记位暂停
*/
public class RecoveryData extends Data implements Configable, Responsable {

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
		return MainCode.RecoveryCode;
	}

}
