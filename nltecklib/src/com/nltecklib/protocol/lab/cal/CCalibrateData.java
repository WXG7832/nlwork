package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.lab.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.lab.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CCalibrateData extends Data implements Queryable, Configable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private CalMode workMode = CalMode.SLEEP;
	private byte readyState = 0;
	private ErrCode errCode = ErrCode.NORMAL;
	private int precisionLevel; // 使用高精度
	private long programV;// 程控电压
	private long programI;// 程控电流

	public CCalibrateData() {

	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());

		data.add((byte) (precisionLevel));
		
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programV, 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programI, 2, true)));

		data.add(readyState);
		data.add((byte) errCode.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

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
		workMode = CalMode.values()[pos];
		// 双精度
		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// 准备状态
		readyState = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ErrCode.values().length - 1) {

			throw new RuntimeException("error code index : " + code);
		}
		// 故障码
		errCode = ErrCode.values()[code];

	}

	@Override
	public Code getCode() {

		return CalCode.CAL;
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

	public CalMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public byte getReadyState() {
		return readyState;
	}

	public void setReadyState(byte readyState) {
		this.readyState = readyState;
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

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}

	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

	@Override
	public String toString() {
		return "CCalibrateData [workState=" + workState + ", workMode=" + workMode + ", readyState=" + readyState
				+ ", errCode=" + errCode + ", precisionLevel=" + precisionLevel + ", programV=" + programV
				+ ", programI=" + programI + "]";
	}

}
