package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 下午3:30:48
* 心跳协议
*/
public class MbHeartbeatData extends Data implements Configable, Responsable {

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
		return CalBoxDeviceCode.HeartbeatCode;
	}

}
