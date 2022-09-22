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
 * @Description: 洛伦兹电源故障信息查询 0x36 支持查询
 * @author: JenHoard_Shaw
 * @date: 2022年3月23日 上午10:05:06
 *
 */
public class PowerLLZErrorInfoData extends Data implements Queryable, Responsable {

	private boolean communicationOk;// 通信故障
	private boolean insideOk;// 内部故障

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) (communicationOk ? 0x00 : 0x01));
		data.add((byte) (insideOk ? 0x00 : 0x01));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		communicationOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		insideOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;

	}

	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public Code getCode() {

		return AccessoryCode.PowerLLZErrorInfoCode;
	}

	public boolean isCommunicationOk() {
		return communicationOk;
	}

	public void setCommunicationOk(boolean communicationOk) {
		this.communicationOk = communicationOk;
	}

	public boolean isInsideOk() {
		return insideOk;
	}

	public void setInsideOk(boolean insideOk) {
		this.insideOk = insideOk;
	}

}
