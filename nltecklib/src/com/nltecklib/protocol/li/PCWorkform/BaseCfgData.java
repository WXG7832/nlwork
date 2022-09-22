package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 上午11:36:44 类说明
 */
public class BaseCfgData extends Data implements Configable, Queryable, Responsable {

	private String deviceIp = ""; // 设备主控
	private List<String> meterIps = new ArrayList<>();// 万用表ip
	private String screenIp = "";// 液晶屏ip

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

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public List<String> getMeterIps() {
		return meterIps;
	}

	public void setMeterIps(List<String> meterIps) {
		this.meterIps = meterIps;
	}

	public String getScreenIp() {
		return screenIp;
	}

	public void setScreenIp(String screenIp) {
		this.screenIp = screenIp;
	}

	@Override
	public void encode() {

		data.addAll(ProtocolUtil.encodeIp(deviceIp));
		data.add((byte) meterIps.size());
		for (int n = 0; n < meterIps.size(); n++) {
			
			
			data.addAll(ProtocolUtil.encodeIp(meterIps.get(n)));
		}
		data.addAll(ProtocolUtil.encodeIp("192.168.1.100"));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		deviceIp = ProtocolUtil.decodeIp(data.subList(index, index + 4));
		index += 4;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {
			meterIps.add(ProtocolUtil.decodeIp(data.subList(index, index + 4)));
			index += 4;
		}
		screenIp = ProtocolUtil.decodeIp(data.subList(index, index + 4));
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.BaseCfgCode;
	}

	@Override
	public String toString() {
		return "BaseCfgData [deviceIp=" + deviceIp + ", meterIps=" + meterIps + ", screenIp=" + screenIp + "]";
	}

}
