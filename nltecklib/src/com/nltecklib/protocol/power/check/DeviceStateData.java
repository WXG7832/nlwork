/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.DeviceState;
import com.nltecklib.protocol.power.check.CheckEnvironment.PickupState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 设备状态 0x0B
 * @version: v1.0.0
 * @date: 2022年1月4日 上午11:08:32
 *
 */
public class DeviceStateData extends Data implements Queryable, Responsable {

	private DeviceState deviceState;

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) deviceState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > DeviceState.values().length - 1) {

			throw new RuntimeException("error deviceState code : " + code);
		}
		deviceState = DeviceState.values()[code];
	}

	@Override
	public Code getCode() {
		return CheckCode.DeviceStateCode;
	}

	public DeviceState getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(DeviceState deviceState) {
		this.deviceState = deviceState;
	}

}
