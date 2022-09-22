package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.Pole;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * 逻辑板基准模式配置和查询
 * @author Administrator
 *
 */
public class CalLogicModeData extends Data implements Configable, Queryable, Responsable {
    
	
	private Pole pole = Pole.NORMAL;
	private WorkMode workMode = WorkMode.CCC;
	private long programV;
	private long programI;
	private int precision; //高精度?
	
	
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
		//逻辑板号
		data.add((byte) unitIndex);
		//通道号
		data.add((byte) chnIndex);
		//对应的板号
		data.add((byte) pole.ordinal());
		data.add((byte) workMode.ordinal());
		if(isDoubleResolutionSupport()) {
			
			//支持双精度
			data.add((byte) precision);
		}
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programV , 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split(programI , 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
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
		//双精度
		if(isDoubleResolutionSupport()) {
			
			precision =  ProtocolUtil.getUnsignedByte(data.get(index++)) ;
		}
		
		// 程控电压
		programV =  ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) ;
		index += 2;
		programI =  ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) ;
		index += 3;

	}

	@Override
	public Code getCode() {
		
		return WorkformCode.LogicModeCode;
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

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}



}
