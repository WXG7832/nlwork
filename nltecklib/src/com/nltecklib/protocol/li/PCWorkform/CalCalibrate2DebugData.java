package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalCalibrate2DebugData extends Data implements Queryable, Configable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private WorkMode workMode = WorkMode.SLEEP;
	private Pole pole = Pole.NORMAL;
	private int programV;
	private int programI;
	private int precision; // 默认高精度

	public CalCalibrate2DebugData() {

	}

	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex); // 校准板号
		data.add((byte) chnIndex); // 通道号
		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());

		data.add((byte) precision);

		data.add((byte) pole.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split(programV, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(programI, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道号
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > WorkState.values().length - 1) {

			throw new RuntimeException("error work state index : " + pos);
		}
		workState = WorkState.values()[pos];
		// 工作模式
		pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > CalMode.values().length - 1) {

			throw new RuntimeException("error work mode index : " + pos);
		}
		workMode = WorkMode.values()[pos];

		precision = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 极性
		pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > Pole.values().length - 1) {

			throw new RuntimeException("error pole index : " + pos);
		}
		pole = Pole.values()[pos];

		programV = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		programI = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {

		return PCWorkformCode.CalCalibrate2DebugCode;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public int getProgramV() {
		return programV;
	}

	public void setProgramV(int programV) {
		this.programV = programV;
	}

	public int getProgramI() {
		return programI;
	}

	public void setProgramI(int programI) {
		this.programI = programI;
	}

	@Override
	public boolean supportDriver() {

		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	public WorkState getWorkState() {
		return workState;
	}

	public void setWorkState(WorkState workState) {
		this.workState = workState;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public String toString() {
		return "Calibrate2Data [workState=" + workState + ", workMode=" + workMode + ", pole=" + pole + ", programV="
				+ programV + ", programI=" + programI + ", precision=" + precision + "]";
	}

}
