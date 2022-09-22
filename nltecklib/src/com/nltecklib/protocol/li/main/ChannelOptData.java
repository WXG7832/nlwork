package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnOpt;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * 通道操作测试
 * 
 * @author Administrator
 *
 */
public class ChannelOptData extends Data implements Configable, Responsable {

	private ChnOpt chnOpt = ChnOpt.STOP;
	// 板通道映射
	private Map<Integer, Short> channels = new HashMap<Integer, Short>();
	private int channelCount;

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) chnOpt.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) channelCount, 2, true)));

		if (unitIndex == 0xff) {
			for (int i = 0; i < channelCount / Data.getDriverChnCount(); i++) {

				Short val = channels.get(i);
				if (val == null) {
					val = 0;
				}
				if (Data.getDriverChnCount() == 16) {
					data.add((byte) (val >> 8 & 0xff));
				}
				data.add((byte) (val & 0xff));
			}
		} else {

			Short val = channels.get(unitIndex);
			if (val == null) {
				val = 0;
			}
			if (Data.getDriverChnCount() == 16) {
				data.add((byte) (val >> 8 & 0xff));
			}
			data.add((byte) (val & 0xff));
		}

		// System.out.println(Entity.printList(data));

	}

	public void clear() {

		channels.clear();
		for (int i = 0; i < channelCount / 8; i++) {

			channels.put(i, (short) 0);
		}
	}

	public void addChannel(int chnIndex) {

		int driverIndex = chnIndex / Data.getDriverChnCount();
		int flag = chnIndex % Data.getDriverChnCount();
		Short val = channels.get(driverIndex);
		if (val == null) {

			val = 0;
		}
		val = (short) (val | (0x01 << flag));
		channels.put(driverIndex, val);
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ChnOpt.values().length - 1) {

			throw new RuntimeException("error chn opt code :" + code);
		}
		chnOpt = ChnOpt.values()[code];
		// 解码标志位

		channelCount = (int) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		int count = index;
		if ((encodeData.size() - count) * 8 != channelCount) {

			throw new RuntimeException("error chn count :" + (encodeData.size() - count) * 8 + "!= " + channelCount);
		}

		if (unitIndex == 0xff) {
			for (int i = 0; i < channelCount / Data.getDriverChnCount(); i++) {

				short val = (short) (Data.getDriverChnCount() == 16
						? ProtocolUtil.getUnsignedByte(data.get(index)) << 8
								| ProtocolUtil.getUnsignedByte(data.get(index + 1))
						: ProtocolUtil.getUnsignedByte(data.get(index)));
				channels.put(i, val);
				index += Data.getDriverChnCount() / 8;
			}
		} else {
             
			short val = (short) (Data.getDriverChnCount() == 16
					? ProtocolUtil.getUnsignedByte(data.get(index)) << 8
							| ProtocolUtil.getUnsignedByte(data.get(index + 1))
					: ProtocolUtil.getUnsignedByte(data.get(index)));
			channels.put(unitIndex, val);
		}

	}

	@Override
	public Code getCode() {

		return MainCode.ChnOperateCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public boolean supportUnit() {
		return true;
	}

	public ChnOpt getChnOpt() {
		return chnOpt;
	}

	public void setChnOpt(ChnOpt chnOpt) {
		this.chnOpt = chnOpt;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int chnCount) {
		this.channelCount = chnCount;
	}

	@Override
	public String toString() {

		StringBuffer str = new StringBuffer();
		for (Iterator<Integer> it = channels.keySet().iterator(); it.hasNext();) {

			Integer driverIndex = it.next();
			str.append(driverIndex + " = " + channels.get(driverIndex) + ",");
		}

		return "ChannelOptData " + str.toString();
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

}
