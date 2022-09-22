/**
 * 
 */
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
 * @Description: 针床校准模式开关 0x3C 支持配置,支持查询
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年8月4日 上午10:56:00
 *
 */
public class PingCalibrateSwitchData extends Data implements Configable, Queryable, Responsable {

	private boolean open;

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

		data.add((byte) unitIndex);
		data.add((byte) (open ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		open = data.get(index++) == 0x01;
	}

	@Override
	public Code getCode() {
		return AccessoryCode.PingCalibrateSwitchCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public String toString() {
		return "PingCalibrateSwitchData [open=" + open + "]";
	}

}
