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
 * 流程步次配置;
 * 步次配置已经和启动结合在一起，故废弃；
 * @author Administrator
 *
 */
@Deprecated
public class LogicProcedureData extends Data implements Configable,Queryable,Responsable{
    
	private WorkMode  workMode = WorkMode.UDT;
	private double    programVoltage;
	private double    programCurrent;
	private double    overThreshold;
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public void encode() {
		
       data.add((byte)unitIndex);
       //工作模式
       data.add((byte)workMode.ordinal());
       //程控电压
       data.addAll(Arrays.asList(ProtocolUtil.split((long)(programVoltage * 10), 2, true)));
       //程控电流
       data.addAll(Arrays.asList(ProtocolUtil.split((long)(programCurrent * 10), 3, true)));
       //结束条件
       data.addAll(Arrays.asList(ProtocolUtil.split((long)(overThreshold * 10), 3, true)));
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
	    overThreshold =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
	    index += 3;
	    
	}

	@Override
	public Code getCode() {
		
		return LogicCode.ChnStopCode;
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

	public double getOverThreshold() {
		return overThreshold;
	}

	public void setOverThreshold(double overThreshold) {
		this.overThreshold = overThreshold;
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
		return "LogicProcedureData [workMode=" + workMode + ", programVoltage=" + programVoltage + ", programCurrent="
				+ programCurrent + ", overThreshold=" + overThreshold + "]";
	}
	
	

}
