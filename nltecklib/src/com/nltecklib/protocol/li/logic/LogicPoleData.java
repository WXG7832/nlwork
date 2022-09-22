package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;


public class LogicPoleData extends Data implements Configable,Queryable,Responsable{
   
	private Pole    pole = Pole.NORMAL;
	private double  voltageBound; //高于该值一律判定为电池负载；否则为空载
	
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex); // 查询，回复都要分区序号
		data.add((byte) pole.ordinal()); //极性
		//界定值正负，正数0，负数1
		data.add((byte) (voltageBound >= 0 ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(Math.abs(voltageBound * 10)), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int poleIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(poleIndex > Pole.values().length - 1) {
			
			throw new RuntimeException("error pole index(" + poleIndex +") in logicBoard(" + (unitIndex + 1) + ")");	
		}
		boolean minus =  ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		voltageBound = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		if(minus) {
			
			voltageBound = -voltageBound;
		}

	}

	@Override
	public Code getCode() {
		
		return LogicCode.PoleCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public double getVoltageBound() {
		return voltageBound;
	}

	public void setVoltageBound(double voltageBound) {
		this.voltageBound = voltageBound;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "LogicPoleData [pole=" + pole + ", voltageBound=" + voltageBound + "]";
	}
	
	

}
