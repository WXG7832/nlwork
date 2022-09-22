package com.nltecklib.protocol.li.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 通道流程跳转指令
 * @author admin
 */
public class DriverChangeStepData extends Data implements Configable, Responsable{
	
	private int loopIndex;//循环号
	private int nextStep;//下步次
	
	private int stepIndex;//当前步次
	private long stepTime;//当前步次耗时 ms
	
	private WorkMode workMode;


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
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		data.add((byte) loopIndex);
		data.add((byte) nextStep);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(code > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error work mode code : " + code);
		}
		workMode = WorkMode.values()[code];
		
		stepTime = ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
		
	}

	@Override
	public Code getCode() {
		return DriverCode.TransferProcessCode;
	}
	

	public int getLoopIndex() {
		return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}

	public int getNextStep() {
		return nextStep;
	}

	public void setNextStep(int nextStep) {
		this.nextStep = nextStep;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public long getStepTime() {
		return stepTime;
	}

	public void setStepTime(long stepTime) {
		this.stepTime = stepTime;
	}
	
	
	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

}
