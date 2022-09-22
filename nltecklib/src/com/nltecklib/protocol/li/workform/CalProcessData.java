package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.Pole;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 逻辑板校准主要通信协议
 * @author Administrator
 *
 */
public class CalProcessData extends Data implements Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private WorkMode workMode = WorkMode.SLEEP;
	private long programV;
	private long programI;
	private int chnIndex;
	private byte ready;
	private ErrCode errCode = ErrCode.NORMAL;
	private boolean highPrecision; //高精度?

	private double primitiveADC;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		//校准板号
		data.add((byte) driverIndex);
		//通道号
		data.add((byte) chnIndex);
		
		// READY
		data.add(ready);
		//errCode
		data.add((byte) errCode.ordinal());
		// ADC原始值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (primitiveADC * 100), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//校准板号
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// ready
		ready = data.get(index++);
		//errCode
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= ErrCode.values().length) {

			throw new RuntimeException("the errCode value is error:" + flag);
		}
		errCode = ErrCode.values()[flag];
		// primitiveADC
		primitiveADC = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;
		

	}

	@Override
	public Code getCode() {
		
		return WorkformCode.LogicCalCode;
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

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}
	
	

	public boolean isHighPrecision() {
		return highPrecision;
	}

	public void setHighPrecision(boolean highPrecision) {
		this.highPrecision = highPrecision;
	}

	@Override
	public String toString() {
		return "CalProcessData [pole=" + pole + ", workMode=" + workMode + ", programV=" + programV + ", programI="
				+ programI + ", chnIndex=" + chnIndex + ", ready=" + ready + ", primitiveADC="
				+ primitiveADC + "]";
	}

}
