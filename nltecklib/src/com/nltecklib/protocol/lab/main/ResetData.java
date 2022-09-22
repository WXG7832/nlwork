package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;

/**
* @author  wavy_zheng
* @version 创建时间：2020年6月23日 下午1:46:32
* 类说明
*/
public class ResetData extends Data implements Configable, Responsable {

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
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ResetCode;
	}

}
