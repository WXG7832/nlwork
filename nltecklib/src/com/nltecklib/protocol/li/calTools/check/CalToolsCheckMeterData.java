package com.nltecklib.protocol.li.calTools.check;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.AdcGroup;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.PickMeterWork;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsCheckMeterData extends Data implements Configable, Queryable, Responsable {

	private PickMeterWork work = PickMeterWork.NONE; // 工作模式
	private List<AdcGroup> adcs = new ArrayList<>(); // 电压检测值n(只读)

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) chnIndex);

		// 工作模式
		data.add((byte) work.ordinal());

		// 电压ADC采集个数
		data.add((byte) adcs.size());

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		/** 工作模式 */
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > PickMeterWork.values().length - 1) {

			throw new RuntimeException("error pickMeterWork code :" + code);
		}
		work = PickMeterWork.values()[code];

		/** 电压ADC采集个数 */
		int adcCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int i = 0; i < adcCount; i++) {
			AdcGroup group = new AdcGroup();
			group.originalAdc = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			group.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;
			adcs.add(group);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsCheckCode.MeterCode;
	}

	public PickMeterWork getWork() {
		return work;
	}

	public void setWork(PickMeterWork work) {
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
		return "CalToolsLogicMeterData [work=" + work.name() + ", adcs=" + adcs + "]";
	}

}
