package com.nltecklib.protocol.lab.mbcal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.lab.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalMode;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午10:03:30
* 类说明
*/
public class MbNewCalibrateData extends Data implements Configable, Queryable, Responsable {
   
	private WorkState workState = WorkState.UNWORK;
	private CalMode workMode = CalMode.SLEEP;
	private byte readyState = 0;
	private long programV;// 程控电压
	private long programI;// 程控电流
	private ErrCode errCode = ErrCode.NORMAL;
	private int precisionLevel; // 使用高精度
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
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
		
		return MbCalCode.CAL2;
	}

	public WorkState getWorkState() {
		return workState;
	}

	public void setWorkState(WorkState workState) {
		this.workState = workState;
	}

	public CalMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
	}

	public byte getReadyState() {
		return readyState;
	}

	public void setReadyState(byte readyState) {
		this.readyState = readyState;
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
		return "MbNewCalibrateData [workState=" + workState + ", workMode=" + workMode + ", readyState=" + readyState
				+ ", programV=" + programV + ", programI=" + programI + ", errCode=" + errCode + ", precisionLevel="
				+ precisionLevel + "]";
	}
	
	

}
