package com.nltecklib.protocol.li.MBWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.check2.Check2Environment.AdcGroup;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.check2.Check2Environment.Work;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月31日 下午2:21:31 类说明
 */
public class MBCheckCalculateData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private VoltMode voltMode = VoltMode.Backup;
	private Work work = Work.NONE;
	private List<AdcGroup> adcs = new ArrayList<>();

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
		return true;
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		// 极性
		data.add((byte) pole.ordinal());
		// 电压模式
		data.add((byte) voltMode.ordinal());
		// 工作方式
		data.add((byte) work.ordinal());
		// 配置需要采集的样本数(1-50)
		data.add((byte) adcs.size());

		for (AdcGroup group : adcs) {
			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (group.finalAdc * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(
					Arrays.asList(ProtocolUtil.split((long) (group.adc1 * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(
					Arrays.asList(ProtocolUtil.split((long) (group.adc2 * Math.pow(10, voltageResolution)), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code :" + code);
		}
		pole = Pole.values()[code];

		// 电压模式
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > VoltMode.values().length - 1) {

			throw new RuntimeException("error voltwork code :" + code);
		}
		voltMode = VoltMode.values()[code];

		// 工作方式
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Work.values().length - 1) {

			throw new RuntimeException("error work code :" + code);
		}
		work = Work.values()[code];

		// sample count
		int count = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int n = 0; n < count; n++) {

			AdcGroup group = new AdcGroup();
			group.finalAdc = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, voltageResolution);
			index += 3;
			group.adc1 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, voltageResolution);
			index += 3;
			group.adc2 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, voltageResolution);
			index += 3;
			adcs.add(group);
		}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MBWorkformCode.CheckCalculateCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public VoltMode getVoltMode() {
		return voltMode;
	}

	public void setVoltMode(VoltMode voltMode) {
		this.voltMode = voltMode;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public List<AdcGroup> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<AdcGroup> adcs) {
		this.adcs = adcs;
	}

	@Override
	public String toString() {
		return "MBCheckCalculateData [pole=" + pole + ", voltMode=" + voltMode + ", work=" + work + ", adcs=" + adcs
				+ "]";
	}

}
