/**
 * 
 */
package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 继电器控制开关 可读可写 0x13 
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年8月10日 下午4:36:26
 *
 */
public class RelaySwitchData extends Data implements Configable, Queryable, Responsable {

	private byte channel;// 校准板通道  0表示关闭所有通道

	@Override
	public boolean supportUnit() {
		return false;
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
	public void encode() {

		data.add((byte) driverIndex);
		data.add(channel);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		channel = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		return CalCode.RelaySwitch;
	}

	public byte getChannel() {
		return channel;
	}

	public void setChannel(byte channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "RelaySwitchData [channel=" + channel + "]";
	}

}
