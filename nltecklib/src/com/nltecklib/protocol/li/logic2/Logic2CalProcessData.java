package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalibrateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Logic2CalProcessData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private CalMode workMode;
	private long programV;
	private long programI;
	private int precision; // 默认高精度

	private List<CalibrateAdcGroup> adcs = new ArrayList<>(); // 采集的ADC集合

	public Logic2CalProcessData() {

	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {

		return false;
	}

	@Override
	public void encode() {
		data.add((byte) unitIndex);
		// 通道序号
		data.add((byte) (isReverseDriverChnIndex()
				? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: chnIndex));
		data.add((byte) pole.ordinal());
		data.add((byte) (workMode.ordinal())); // 兼容逻辑板+1
		// 高精度支持
		data.add((byte) precision);

		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programV), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programI), 3, true)));

		data.add((byte) adcs.size());
//		for(int n = 0 ; n < adcs.size() ; n++) {
//		    // ADC原始值;单位0.01mV
//		   data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcs.get(n).adc * 100), 3, true)));
//		   data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcs.get(n).adc1 * 100), 3, true)));
//		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(val, Data.getDriverChnCount()) : val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag > CalMode.values().length - 1) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = CalMode.values()[flag]; // 为了兼容逻辑板程序+1；解码时-1

		precision = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
		// ready
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		adcs.clear();
		for (int n = 0; n < count; n++) {

			CalibrateAdcGroup group = new CalibrateAdcGroup();
			group.adc2 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, currentResolution);
			index += 3;
			group.adc1 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, currentResolution);
			index += 3;
			adcs.add(group);
		}

	}

	@Override
	public Code getCode() {

		return Logic2Code.ChnCalCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public CalMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
	}

	public long getProgramV() {
		return programV;
	}

	public void setProgramV(long programV) {
		this.programV = programV;
	}

	public long getProgramI() {
		return programI;
	}

	public void setProgramI(long programI) {
		this.programI = programI;
	}

	public List<CalibrateAdcGroup> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<CalibrateAdcGroup> adcs) {
		this.adcs = adcs;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public String toString() {
		return "Logic2CalProcessData [pole=" + pole + ", workMode=" + workMode + ", programV=" + programV
				+ ", programI=" + programI + ", chnIndex=" + chnIndex + ", precision=" + precision + ", adcs=" + adcs
				+ "]";
	}

}
