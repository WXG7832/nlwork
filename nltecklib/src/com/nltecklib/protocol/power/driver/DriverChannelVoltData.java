package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 通道电压查询  0x37
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年8月24日 上午9:49:43
 *
 */
public class DriverChannelVoltData extends Data implements Queryable, Responsable {

	private List<ChnDataPack> packs = new ArrayList<>();

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) packs.size());

		for (int n = 0; n < packs.size(); n++) {

			ChnDataPack chnData = packs.get(n);

			// 通道状态
			data.add((byte) chnData.getState().getCode());
			// 工作模式
			data.add((byte) chnData.getWorkMode().ordinal());
			// 步次循环号
			data.add((byte) chnData.getLoopIndex());
			// 步次序号
			data.add((byte) chnData.getStepIndex());
			// 主电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getVoltage() * Math.pow(10, 1)), 3, true)));
			// 步次流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getStepElapsedTime(), 4, true)));

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++)); // 通道包数
		packs.clear();

		for (int n = 0; n < chnCount; n++) {

			ChnDataPack chnData = new ChnDataPack();

			// 通道状态
			int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (ChnState.valueOf(stateIndex) == null) {

				throw new RuntimeException("error channel(" + (n + 1) + ") state index :" + stateIndex);
			}
			chnData.setState(ChnState.valueOf(stateIndex));
			
			// 通道工作模式
			int modeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (modeIndex > WorkMode.values().length - 1) {

				throw new RuntimeException("error work mode index :" + modeIndex);
			}
			chnData.setWorkMode(WorkMode.values()[modeIndex]);

			// 步次循环号
			chnData.setLoopIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			
			// 步次序号
			chnData.setStepIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			
			// 通道电压
			chnData.setVoltage(
					(double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
			index += 3;

			// 步次流逝时间
			chnData.setStepElapsedTime(ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true));
			index += 4;

			packs.add(chnData);

		}

	}

	@Override
	public Code getCode() {

		return DriverCode.ChannelVoltCode;
	}

	public List<ChnDataPack> getPacks() {
		return packs;
	}

	public void setPacks(List<ChnDataPack> packs) {
		this.packs = packs;
	}

	@Override
	public String toString() {
		return "DriverChannelVoltData [packs=" + packs + "]";
	}

}
