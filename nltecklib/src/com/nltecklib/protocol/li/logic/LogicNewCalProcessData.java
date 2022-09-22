package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;


public class LogicNewCalProcessData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private WorkMode workMode = WorkMode.SLEEP;
	private long programV;
	private long programI;
	private int subDA;
	private int chnIndex;
	private byte ready;
	private int precision; 
	private double primitiveADC;
	private double finalADC;
	
	public LogicNewCalProcessData() {
		
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
		data.add((byte) workMode.ordinal());
			
		data.add((byte) (precision));
		
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programV), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programI), 3, true)));
		// READY
		data.add(ready);
		// ADC原始值;单位0.01mV
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (primitiveADC * 100), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= WorkMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = WorkMode.values()[flag];

        precision = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
		// ready
		ready = data.get(index++);
		// primitiveADC
		primitiveADC = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

	}

	@Override
	public Code getCode() {

		return LogicCode.LogicNewCalProcessCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
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

	public int getSubDA() {
		return subDA;
	}

	public void setSubDA(int subDA) {
		this.subDA = subDA;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public double getPrimitiveADC() {
		return primitiveADC;
	}

	public void setPrimitiveADC(double primitiveADC) {
		this.primitiveADC = primitiveADC;
	}

	public double getFinalADC() {
		return finalADC;
	}

	public void setFinalADC(double finalADC) {
		this.finalADC = finalADC;
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
		return "LogicNewCalProcessData [pole=" + pole + ", workMode=" + workMode + ", programV=" + programV
				+ ", programI=" + programI + ", subDA=" + subDA + ", chnIndex=" + chnIndex + ", ready=" + ready
				+ ", precision=" + precision + ", primitiveADC=" + primitiveADC + ", finalADC=" + finalADC + "]";
	}

}
