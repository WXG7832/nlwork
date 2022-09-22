/**
 * 
 */
package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 工控机断电检测 0x1B 支持配置和查询
 * @author: JenHoard_Shaw
 * @date: 2022年5月16日 下午4:07:06
 *
 */
public class IPC_PowerOptData extends Data implements Configable, Queryable, Responsable {

	private PowerOFFSign powerOFFSign;// **断电信号

	/**
	 * **断电信号枚举类
	 */
	public enum PowerOFFSign {

		NORMAL, POWER_OFF, MAINBOARD_CONFIG
	}

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) powerOFFSign.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > PowerOFFSign.values().length - 1) {

			throw new RuntimeException("error powerOFFSign code :" + code);
		}
		powerOFFSign = PowerOFFSign.values()[code];

	}

	@Override
	public Code getCode() {
		return AccessoryCode.IPC_PowerCode;
	}

	public PowerOFFSign getPowerOFFSign() {
		return powerOFFSign;
	}

	public void setPowerOFFSign(PowerOFFSign powerOFFSign) {
		this.powerOFFSign = powerOFFSign;
	}

}
