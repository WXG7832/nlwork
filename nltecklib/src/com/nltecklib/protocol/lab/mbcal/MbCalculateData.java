package com.nltecklib.protocol.lab.mbcal;

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
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午8:29:09
* 类说明
*/
public class MbCalculateData extends Data implements Configable, Queryable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private CalMode workMode = CalMode.SLEEP;
	private int precisionLevel; // 使用高精度
	private double calculateDot;
	private byte readyState = 0;
	private ErrCode errCode = ErrCode.NORMAL;

	public MbCalculateData() {

	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	@Override
	public void encode() {

		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());

		data.add((byte) (precisionLevel));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 1000), 3, true)));

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
		
		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
		index += 3;

		// 准备状态
		readyState = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ErrCode.values().length - 1) {

			throw new RuntimeException("error code index : " + code);
		}
		// 故障码
		errCode = ErrCode.values()[code];

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
		return "MbCalculateData [workState=" + workState + ", workMode=" + workMode + ", precisionLevel="
				+ precisionLevel + ", calculateDot=" + calculateDot + ", readyState=" + readyState + ", errCode="
				+ errCode + "]";
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.CALCULATE;
	}

}
