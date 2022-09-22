package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年2月17日 下午12:33:38 增强型的校准协议
 */
public class PCalibrateExData extends Data implements Configable, Queryable, Responsable {

	private CalWorkMode workMode = CalWorkMode.SLEEP;
	private int range; // 精度档位
	private int programVoltage; // 程控电压量化值
	private int programCurrent; // 程控电流量化值

	public static class AdcData {

		public double primitiveAdc;
		public double primitiveBackAdc;
		public double primitivePowerAdc;

	}

	private List<AdcData> primitiveAdcs = new ArrayList<AdcData>(); // adc原始值,只读

	@Override
	public boolean supportMain() {
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
		// 工作方式
		data.add((byte) workMode.ordinal());
		// 精度档位
		data.add((byte) range);
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programVoltage, 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programCurrent, 2, true)));

		data.add((byte) primitiveAdcs.size());
		// 原始adc，只读

		for (AdcData adc : primitiveAdcs) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitiveAdc * 1000), 4, true)));

			if (workMode == CalWorkMode.CV) {
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitiveBackAdc * 1000), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc.primitiveAdc * 1000), 4, true)));
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		// 工作方式
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalWorkMode.values().length - 1) {

			throw new RuntimeException("error work mode code :" + code);
		}
		workMode = CalWorkMode.values()[code];
		// 档位
		range = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 程控电压
		programVoltage = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 程控电流
		programCurrent = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 原始ADC
		for (int i = 0; i < length; i++) {

			AdcData adc = new AdcData();

			double val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;
			adc.primitiveAdc = val;

			if (workMode == CalWorkMode.CV) {

				val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;
				adc.primitiveBackAdc = val;

				val = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;
				adc.primitivePowerAdc = val;

			}

			primitiveAdcs.add(adc);
		}

	}

	@Override
	public Code getCode() {

		return ChipPickupCode.CalibrateExCode;
	}

	public CalWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalWorkMode workMode) {
		this.workMode = workMode;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getProgramVoltage() {
		return programVoltage;
	}

	public void setProgramVoltage(int programVoltage) {
		this.programVoltage = programVoltage;
	}

	public int getProgramCurrent() {
		return programCurrent;
	}

	public void setProgramCurrent(int programCurrent) {
		this.programCurrent = programCurrent;
	}

	public List<AdcData> getPrimitiveAdcs() {
		return primitiveAdcs;
	}

	public void setPrimitiveAdcs(List<AdcData> primitiveAdcs) {
		this.primitiveAdcs = primitiveAdcs;
	}

	@Override
	public String toString() {
		return "PCalibrateExData [workMode=" + workMode + ", range=" + range + ", programVoltage=" + programVoltage
				+ ", programCurrent=" + programCurrent + ", primitiveAdcs=" + primitiveAdcs + "]";
	}

}
