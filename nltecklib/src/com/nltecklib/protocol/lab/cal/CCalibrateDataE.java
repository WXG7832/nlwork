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
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CCalibrateDataE extends Data implements Queryable, Configable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private CalMode workMode = CalMode.SLEEP;
	private Pole pole = Pole.NORMAL;
	private byte boardIndex;
	private int precisionLevel; // 使用高精度
	private long programV;// 程控电压
	private long programI;// 程控电流

	public CCalibrateDataE() {

	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void encode() {

		data.add((byte) boardIndex); // 校准板号
		data.add((byte) chnIndex); // 通道号
		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());

		data.add((byte) (precisionLevel));
		data.add((byte) pole.ordinal());
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programV, 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programI, 2, true)));

		

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		boardIndex = (byte)ProtocolUtil.getUnsignedByte(data.get(index++));
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
		workMode = CalMode.values()[pos];
		// 双精度
		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		// 极性
		pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > Pole.values().length - 1) {

			throw new RuntimeException("error pole index : " + pos);
		}
		pole = Pole.values()[pos];

		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	
	}

	@Override
	public Code getCode() {

		return CalCode.CAL3;
	}

	public Pole getPole()
	{
		return pole;
	}

	public void setPole(Pole pole)
	{
		this.pole = pole;
	}

	public byte getBoardIndex()
	{
		return boardIndex;
	}

	public void setBoardIndex(byte boardIndex)
	{
		this.boardIndex = boardIndex;
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

	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public WorkState getWorkState() {
		return workState;
	}

	public void setWorkState(WorkState workState) {
		this.workState = workState;
	}


	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

	@Override
	public String toString() {
		return "CCalibrateDataE [workState=" + workState + ", workMode=" + workMode + ", chNo=" + chnIndex
				+ ", boardIndex=" + boardIndex + ", precisionLevel=" + precisionLevel + ", programV=" + programV
				+ ", programI=" + programI + "]";
	}

}
