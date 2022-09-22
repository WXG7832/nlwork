package com.nltecklib.protocol.li.check;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.Work;
import com.nltecklib.protocol.li.check.CheckEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;


//25:计量通道 0x19  ，支持配置，支持查询
public class CheckCalculateData extends Data implements Configable, Queryable, Responsable  {

	private Pole pole = Pole.NORMAL;
	private byte ready = 0;
	private double  adc;
	private WorkMode  workMode;
	private double  calculateDot; //计量点
	
    
	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}


	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}


	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
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
		// TODO Auto-generated method stub
		data.add((byte) unitIndex);
		data.add(isReverseDriverChnIndex()
				? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: (byte) chnIndex);
		// 极性
		data.add((byte) pole.ordinal());
		// 工作方式
		data.add((byte) workMode.ordinal());
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 100), 3, true)));
		// ready
		data.add( ready);
		//检测值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 100), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {
			// 驱动板通道反序
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code :" + code);
		}
		pole = Pole.values()[code];
		// 工作方式
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Work.values().length - 1) {

			throw new RuntimeException("error work code :" + code);
		}
		workMode = WorkMode.values()[code];
		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

		// ready
		ready = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		
		adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;	
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.CalculateCode;
	}

	@Override
	public String toString() {
		return "CheckCalculateData [pole=" + pole + ", ready=" + ready + ", adc=" + adc + ", workMode=" + workMode
				+ ", calculateDot=" + calculateDot + "]";
	}

}
