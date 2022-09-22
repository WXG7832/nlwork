package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 托盘气阀控制开关： 0x3B 可读可写
 * @author: JenHoard_Shaw
 * @date: 2022年6月17日 上午9:20:11
 *
 */
public class AirValveSwitchData extends Data implements Configable, Queryable, Responsable {

	private SwitchState switchState = SwitchState.CLOSE;// 托盘气阀控制开关

	public enum SwitchState {

		CLOSE, OPEN
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
	public boolean supportUnit() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) switchState.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code > SwitchState.values().length - 1) {

			throw new RuntimeException("error SwitchState code:" + code);
		}
		switchState = SwitchState.values()[code];

	}

	@Override
	public Code getCode() {
		return AccessoryCode.AirValveSwitchCode;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

}
