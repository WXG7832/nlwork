package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准协议
 * 
 * @author Administrator
 *
 */
public class PCalibrateData extends Data implements Configable, Queryable, Responsable {

	private CalWorkMode workMode = CalWorkMode.SLEEP;
	private int group; // 精度档位
	private int programVoltage; // 程控电压量化值
	private int programCurrent; // 程控电流量化值
	private List<Double> primitiveAdcs = new ArrayList<Double>(); // adc原始值,只读

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
		data.add((byte) group);
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programVoltage, 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programCurrent, 2, true)));

		data.add((byte) primitiveAdcs.size());
		// 原始adc，只读

		for (double val : primitiveAdcs) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (val * 1000), 3, true)));
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
		group = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 程控电压
		programVoltage = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 程控电流
		programCurrent = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 原始ADC
		for (int i = 0; i < length; i++) {

			double val = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;
			primitiveAdcs.add(val);
		}

	}

	@Override
	public Code getCode() {

		return ChipPickupCode.CalibrateCode;
	}

	public CalWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalWorkMode workMode) {
		this.workMode = workMode;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
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



	public List<Double> getPrimitiveAdcs() {
		return primitiveAdcs;
	}

	public void setPrimitiveAdcs(List<Double> primitiveAdcs) {
		this.primitiveAdcs = primitiveAdcs;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "CalibrateData [workMode=" + workMode + ", group=" + group + ", programVoltage=" + programVoltage
				+ ", programCurrent=" + programCurrent + ", primitiveAdcs=" + primitiveAdcs + "]";
	}
	
	

}
