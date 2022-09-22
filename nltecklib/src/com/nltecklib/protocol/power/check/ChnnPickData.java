/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.PickupState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 通道采集启动、停止功能码 0x02支持配置
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月29日 上午10:10:09
 *
 */
public class ChnnPickData extends Data implements Configable, Responsable {

	private PickupState pickupState;

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) pickupState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > PickupState.values().length - 1) {

			throw new RuntimeException("error pickupState code : " + code);
		}
		pickupState = PickupState.values()[code];
	}

	@Override
	public Code getCode() {
		return CheckCode.ChnnPickCode;
	}

	public PickupState getPickupState() {
		return pickupState;
	}

	public void setPickupState(PickupState pickupState) {
		this.pickupState = pickupState;
	}

}
