package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月6日 下午5:22:49
* 类说明
*/
public class CalCalculate2DebugData extends Data implements Configable, Queryable, Responsable {
    
	private WorkState workState = WorkState.UNWORK;
	private WorkMode workMode = WorkMode.SLEEP;
	private Pole pole = Pole.NORMAL;
	private double  calculateDot;
	private int precision; // 默认高精度
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
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
		
		data.add((byte) driverIndex); // 校准板号
		data.add((byte) chnIndex); // 通道号
		data.add((byte) workState.ordinal());
		data.add((byte) workMode.ordinal());
		
		data.add((byte) precision);

		
		data.add((byte) pole.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(calculateDot * 100), 3, true)));
		

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
		if (pos > WorkMode.values().length - 1) {

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

		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index+=3;
		
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalCalculate2DebugCode;
	}

	public WorkState getWorkState() {
		return workState;
	}

	public void setWorkState(WorkState workState) {
		this.workState = workState;
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

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	

}
