package com.nltecklib.protocol.li.logic;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class LogicChnSwitchData extends Data implements Configable, Queryable, Responsable {

	private List<Byte> channelStates = new ArrayList<Byte>();

	public static final int CHN_STATE_OPEN = 0x01;
	public static final int CHN_STATE_CLOSE = 0x00;

	public LogicChnSwitchData() {

	}

	@Override
	public String toString() {
		return "ChannelStateData [unitIndex=" + unitIndex + ", channelStates=" + Entity.printList(channelStates) + "]";
	}

	public void init(int count, boolean close) {

		if (count % 8 != 0 || count < 8 || count > 256) {

			throw new RuntimeException("分区通道数必须在8-256之间且是8的整数倍!");
		}
		Byte b = new Byte((byte) (close ? 0x00 : 0xff));
		for (int i = 0; i < count / 8; i++) {

			channelStates.add(b);
		}
	}

	public void setState(int index, boolean close) {

		// 1 byte 占据 8位通道

		int n = index / 8;
		// 第一个字节为
		int val = channelStates.get(n) & 0x0ff;

		int m = index % 8;

		if (close) {
			val = val & ~(0x01 << m);

		} else {
			val = val | (0x01 << m);
		}
		channelStates.set(n, (byte) val);

	}

	public boolean isChnClosed(int index) {

		int groupIndex = index / 8;
		int val = channelStates.get(groupIndex) & (0x01 << (index % 8));
		return val == CHN_STATE_CLOSE;
	}

	@SuppressWarnings("unused")
	@Override
	public void encode() {

		data.add((byte) unitIndex);

		int count = Data.getDriverChnCount() / 8;

		if (Data.getDriverChnCount() > 16) {

			throw new RuntimeException("not surpport driver channel count : " + Data.getDriverChnCount());
		}

		// 低位在前，高位在后
		for (int i = 0; i < channelStates.size(); i += count) {

			if (count == 1) {

				data.add(isReverseDriverChnIndex()
						? ProtocolUtil.reverseByteBit(channelStates.get(i), Data.getDriverChnCount())
						: channelStates.get(i));
			} else if (count == 2) {

				short val = (short) (channelStates.get(i + 1) << 8 | channelStates.get(i));
				val = isReverseDriverChnIndex() ? ProtocolUtil.reverseShortBit(val, Data.getDriverChnCount()) : val;
				data.add((byte) (val & 0xff));
				data.add((byte) (val >> 8 & 0xff));

			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (Data.getDriverChnCount() > 16) {

			throw new RuntimeException("not surpport driver channel count : " + Data.getDriverChnCount());
		}

		int count = Data.getDriverChnCount() / 8;
		// 低位在前，高位在后
		for (int i = index; i < encodeData.size(); i += count) {

			if (count == 1) {
				channelStates.add(isReverseDriverChnIndex()
						? ProtocolUtil.reverseByteBit(encodeData.get(i), Data.getDriverChnCount())
						: encodeData.get(i));
			} else if (count == 2) {

				short val = (short) (encodeData.get(i + 1) << 8 | encodeData.get(i));
				val = isReverseDriverChnIndex() ? ProtocolUtil.reverseShortBit(val, Data.getDriverChnCount()) : val;
				channelStates.add((byte) (val & 0xff));
				channelStates.add((byte) (val >> 8 & 0xff));

			}
		}

	}

	@Override
	public Code getCode() {

		return LogicCode.ChnSwitchCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public List<Byte> getChannelStates() {
		return channelStates;
	}

	public void setChannelStates(List<Byte> channelStates) {
		this.channelStates = channelStates;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

	/**
	 * @param index 分区通道数-通道号；比如点击数字1，index = 分区通道数 - 1；
	 * @return true是打开，反之关闭
	 */
	public boolean getState(int index) {
		int n = (getLogicDriverCount() * getDriverChnCount() - index - 1) / 8;
		int val = channelStates.get(n) & 0x0ff;
		int m = index % 8;
		val = (val >> (7 - m)) & 0x01;
		if (val == 1) {
			return true;
		}
		return false;
	}
}
