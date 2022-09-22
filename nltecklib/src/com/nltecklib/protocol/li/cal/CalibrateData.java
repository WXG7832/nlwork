package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.Pole;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.util.ProtocolUtil;


public class CalibrateData extends Data implements Queryable, Configable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private WorkMode workMode = WorkMode.SLEEP;
	private Pole pole = Pole.NORMAL;
	private byte readyState = 0;
	private ErrCode  errCode = ErrCode.NORMAL;
	private int precision; //默认高精度
	
	
	public CalibrateData() {
		
		
	}
	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex); // 校准板号
		if (isReverseDriverChnIndex()) {

			chnIndex = Data.getDriverChnCount() - 1 - chnIndex;
		}
		data.add((byte) chnIndex); //通道号
		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());
		// 高精度支持
		if (isDoubleResolutionSupport()) {

			data.add((byte)precision);

		} 
		data.add((byte) pole.ordinal());
		data.add(readyState);
		data.add((byte)errCode.ordinal());
		

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道号
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			chnIndex = Data.getDriverChnCount() - 1 - chnIndex;
		}

		int pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > WorkState.values().length - 1) {

			throw new RuntimeException("error work state index : " + pos);
		}
		workState = WorkState.values()[pos];
		// 工作模式
		pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > WorkMode.values().length - 1) {

			throw new RuntimeException("error work mode index : " + pos);
		}
		workMode = WorkMode.values()[pos];
		
		//双精度支持
		if(isDoubleResolutionSupport()) {
			
			precision = ProtocolUtil.getUnsignedByte(data.get(index++)) ;
		
		}
			
		// 极性
		pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > Pole.values().length - 1) {

			throw new RuntimeException("error pole index : " + pos);
		}
		pole = Pole.values()[pos];

		// 准备状态
		readyState = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ErrCode.values().length - 1) {
			
			throw new RuntimeException("error code index : " + code);
		}
		//故障码
		errCode   = ErrCode.values()[code];

	}

	@Override
	public Code getCode() {

		return CalEnvironment.CalCode.CAL;
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

	public byte getReadyState() {
		return readyState;
	}

	public void setReadyState(byte readyState) {
		this.readyState = readyState;
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

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}



	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public String toString() {
		return "CalibrateData [workState=" + workState + ", workMode=" + workMode + ", pole=" + pole + ", readyState="
				+ readyState + ", errCode=" + errCode + ", precision=" + precision + "]";
	}

}
