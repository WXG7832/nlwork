package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月7日 上午11:09:28
* 单纯连接设备
*/
public class ConnectDeviceData extends Data implements Configable, Responsable {
    
	//设备主控Ip
	private String deviceIp;
	//是否已连接设备
	private boolean connect;
	
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
		
		data.addAll(ProtocolUtil.encodeIp(deviceIp));
		data.add((byte) (connect ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		deviceIp = ProtocolUtil.decodeIp(data.subList(index, index+4));
		index += 4;
		connect = data.get(index++) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.ConnectDeviceCode;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	@Override
	public String toString() {
		return "ConnectDeviceData [deviceIp=" + deviceIp + ", connect=" + connect + "]";
	}

	
	
	

}
