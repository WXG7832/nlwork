package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalibrateAdcGroup;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 下午2:44:09 类说明
 */
public class LogicCalibrateDebugData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private CalMode workMode;
	private int precision; // 精度
	private long programV;
	private long programI;
	private List<CalibrateAdcGroup> adcs = new ArrayList<>(); // 采集的ADC集合

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
		return true;
	}


	@Override
	public void encode() {

		data.add((byte) unitIndex);
		// 通道序号
		data.add((byte) chnIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) workMode.ordinal());
		// 高精度支持
		data.add((byte) precision);

		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programV), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programI), 3, true)));

		data.add((byte) adcs.size());
		for (CalibrateAdcGroup adc : adcs) {
			// ADC原始值;单位0.01mV
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.adc2 * Math.pow(10, currentResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.adc1 * Math.pow(10, currentResolution)), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= CalMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = CalMode.values()[flag];

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
			// primitiveADC
			CalibrateAdcGroup group = new CalibrateAdcGroup();
			group.adc2 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, currentResolution);
			index += 3;
			group.adc1 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, currentResolution);
			index += 3;
			adcs.add(group);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.LogicCalibrateDebugCode;
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

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
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
	public String toString() {
		return "LogicCalibrateDebugData [pole=" + pole + ", workMode=" + workMode + ", precision=" + precision
				+ ", programV=" + programV + ", programI=" + programI + ", adcs=" + adcs + "]";
	}

	
}
