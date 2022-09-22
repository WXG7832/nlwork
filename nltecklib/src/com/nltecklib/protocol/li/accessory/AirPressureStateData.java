package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 气压状态（气体监测板专用）： 0x3A 只支持读取
 * @author: JenHoard_Shaw
 * @date: 2022年6月16日 下午1:43:14
 *
 */
public class AirPressureStateData extends Data implements Queryable, Responsable {

	private AirPressureState airPressureState = AirPressureState.NORMAL;// 气压状态

	public enum AirPressureState {

		NORMAL, LOW, HIGH
	}

	@Override
	public boolean supportUnit() {
		return true;
	}

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

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code > AirPressureState.values().length - 1) {

			throw new RuntimeException("error AirPressureState code:" + code);
		}
		airPressureState = AirPressureState.values()[code];

	}

	@Override
	public Code getCode() {
		return AccessoryCode.AirPressureStateCode;
	}

	public AirPressureState getAirPressureState() {
		return airPressureState;
	}

	public void setAirPressureState(AirPressureState airPressureState) {
		this.airPressureState = airPressureState;
	}

	@Override
	public String toString() {
		return "AirPressureStateData [airPressureState=" + airPressureState + "]";
	}
	
	

}
