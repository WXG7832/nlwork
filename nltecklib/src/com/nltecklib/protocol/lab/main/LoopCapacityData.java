package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年11月21日 上午10:25:22
* 针对循环容量进行补偿的一种策略
*/
public class LoopCapacityData extends Data implements Configable, Responsable {
    
	private int stepIndex;
	private WorkMode  workMode; //类型
	private double compensationCapacity; //补偿容量值,单位mAh
	private long    stepTimeout; //最大步次时间,单位ms
	
	
	
	@Override
	public boolean supportMain() {
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
		
		//步次号
		data.addAll(Arrays.asList(ProtocolUtil.split(stepIndex, 2, true)));
		data.add((byte) workMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (compensationCapacity * 10), 3, true)));
        data.addAll(Arrays.asList(ProtocolUtil.split(stepTimeout, 6, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		//步次号
		int index = 0;
		data = encodeData;
    	stepIndex = (int) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
    	index += 2;
    	
    	//工作模式
    	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
    	if(code > WorkMode.values().length - 1) {
    		
    		throw new RuntimeException("error work mode code :" + code);
    	}
    	workMode = WorkMode.values()[code];
    	
    	//补偿容量
    	compensationCapacity =  (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
    	index += 3;
    	
    	//步次流逝时间
    	stepTimeout = ProtocolUtil.compose(data.subList(index, index+6).toArray(new Byte[0]), true);
    	index += 6;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.LoopCapacityCode;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public double getCompensationCapacity() {
		return compensationCapacity;
	}

	public void setCompensationCapacity(double compensationCapacity) {
		this.compensationCapacity = compensationCapacity;
	}

	public long getStepTimeout() {
		return stepTimeout;
	}

	public void setStepTimeout(long stepTimeout) {
		this.stepTimeout = stepTimeout;
	}
	
	

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	@Override
	public String toString() {
		return "LoopCapacityData [stepIndex=" + stepIndex + ", workMode=" + workMode + ", compensationCapacity="
				+ compensationCapacity + ", stepTimeout=" + stepTimeout + "]";
	}

	
	
	

}
