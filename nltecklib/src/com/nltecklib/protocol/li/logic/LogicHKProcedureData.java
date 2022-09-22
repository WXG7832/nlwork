package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
* @author  wavy_zheng
* @version 创建时间：2020年4月24日 上午10:10:11
* HK分区单步次流程配置
*/
public class LogicHKProcedureData extends Data implements Configable, Queryable, Responsable {
     
	private WorkMode  workMode = WorkMode.UDT;
	private double    specialVoltage;
	private double    specialCurrent;
	private double    overtheshold; //截止值
	
	
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
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		//工作方式
		data.add((byte) workMode.ordinal());
		//额定电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (specialVoltage * 10), 2, true)));
		//额定电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (specialCurrent * 10), 3, true)));
		//截止条件
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (overtheshold * 10), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error work mode code : " + code);
		}
		workMode = WorkMode.values()[code];
		//电压
		specialVoltage = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		//电流
		specialCurrent = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		//截止条件
		overtheshold = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return LogicCode.HKProcedureCode;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public double getSpecialVoltage() {
		return specialVoltage;
	}

	public void setSpecialVoltage(double specialVoltage) {
		this.specialVoltage = specialVoltage;
	}

	public double getSpecialCurrent() {
		return specialCurrent;
	}

	public void setSpecialCurrent(double specialCurrent) {
		this.specialCurrent = specialCurrent;
	}

	public double getOvertheshold() {
		return overtheshold;
	}

	public void setOvertheshold(double overtheshold) {
		this.overtheshold = overtheshold;
	}
	
	

}
