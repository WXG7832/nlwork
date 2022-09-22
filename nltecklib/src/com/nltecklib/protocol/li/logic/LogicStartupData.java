package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 启动逻辑板所有通道
 * @author Administrator
 *
 */
public class LogicStartupData extends Data implements Configable, Responsable {
   
	private WorkMode workMode = WorkMode.UDT;
	private double programVoltage;
	private double programCurrent;
	private double endThreshold; // 结束电压电流值
	
	@Override
	public boolean supportUnit() {
	
		return true;
	}

	@Override
	public boolean supportDriver() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		// 工作模式
		data.add((byte) workMode.ordinal());
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programVoltage * 10), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programCurrent * 10), 3, true)));
		// 结束条件
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endThreshold * 10), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
	    unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    int val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    if(val > WorkMode.values().length -1 ) {
	    	
	    	throw new RuntimeException("error workmode index:" + val + "in procedure step configuration");
	    }
	    workMode = WorkMode.values()[val];
	    //程控电压
	    programVoltage =  (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	    //程控电流
	    programCurrent =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
	    index += 3;
	    //结束条件
	    endThreshold =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
	    index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return LogicCode.StartupCode;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public double getProgramVoltage() {
		return programVoltage;
	}

	public void setProgramVoltage(double programVoltage) {
		this.programVoltage = programVoltage;
	}

	public double getProgramCurrent() {
		return programCurrent;
	}

	public void setProgramCurrent(double programCurrent) {
		this.programCurrent = programCurrent;
	}

	public double getEndThreshold() {
		return endThreshold;
	}

	public void setEndThreshold(double endThreshold) {
		this.endThreshold = endThreshold;
	}
	
	

}
