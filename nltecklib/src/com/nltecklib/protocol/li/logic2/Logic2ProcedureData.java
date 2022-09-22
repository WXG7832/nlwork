package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.util.ProtocolUtil;


public class Logic2ProcedureData extends Data implements Configable, Queryable, Responsable {


	private int loopCount; // 0无效
	private int loopSt;
	private int loopEd;
	private List<Step> steps = new ArrayList<Step>();
	
	private static final int STEP_BYTES = 19;

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
       
		data.add((byte) loopCount);
		data.add((byte) loopSt);
		data.add((byte) loopEd);
		data.add((byte) steps.size());

		for (int i = 0; i < steps.size(); i++) {

			Step step = steps.get(i);
			// 工作方式
			data.add((byte) step.workMode.ordinal());
			// 额定恒定电压

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialVoltage * 10), 2, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialCurrent * 10), 3, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.overThreshold * 10), 3, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split(step.overTime, 3, true)));

			data.add((byte) (step.timeProtect ? 1 : 0));

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.deltaVoltage * 10), 2, true)));

			// 容量结束条件

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.overCapacity * 10), 4, true)));


			data.add((byte) step.overMode.ordinal());
		}

	}
	
	

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		long val = 0;
        data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
//        int code = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
//        if(code > StepMode.values().length - 1) {
//        	
//        	throw new RuntimeException("error step mode code:" +  code);
//        }
        

		loopCount = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopSt = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopEd = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		int stepCount = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

		// 解码步次信息，每个步次占21个字节
		if ((encodeData.size() - index) % STEP_BYTES != 0) {

			throw new RuntimeException("非法的报文长度:" + Entity.printList(data));
		}
		if (stepCount != (encodeData.size() - index) / STEP_BYTES) {

			throw new RuntimeException("非法的报文，每步次字节数(" + STEP_BYTES + ")不能整除:" + Entity.printList(data));
		}

		for (int i = 0; i < stepCount; i++) {

			Step step = new Step();
			step.stepIndex = i + 1;
			step.workMode = WorkMode.values()[encodeData.get(index++)]; // 工作方式
			val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
			step.specialVoltage = (double) val / 10;
			index += 2;
			// 额定电流-恒定电流
			val = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
			step.specialCurrent = (double) val / 10;
			index += 3;

			val = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
			step.overThreshold = (double) val / 10;
			index += 3;

			val = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
			step.overTime = (int) val;
			index += 3;

			val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
			step.timeProtect = val == 1;

			val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
			step.deltaVoltage = (double) val / 10;
			index += 2;

			val = ProtocolUtil.compose(encodeData.subList(index, index + 4).toArray(new Byte[0]), true);
			step.overCapacity = (double) val / 10;
			index += 4;


			step.overMode = OverMode.values()[encodeData.get(index++)];

			steps.add(step);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.ProcessCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}



	public List<Step> getSteps() {
		return steps;
	}



	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

    


	@Override
	public String toString() {
		return "Logic2ProcessData [loopCount=" + loopCount + ", loopSt=" + loopSt + ", loopEd=" + loopEd + ", steps="
				+ steps + "]";
	}



	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public int getLoopSt() {
		return loopSt;
	}

	public void setLoopSt(int loopSt) {
		this.loopSt = loopSt;
	}

	public int getLoopEd() {
		return loopEd;
	}

	public void setLoopEd(int loopEd) {
		this.loopEd = loopEd;
	}

	public void addStep(Step step) {

		steps.add(step);
	}

	public void clearSteps() {

		steps.clear();
	}

	public Step getStep(int index) {

		return steps.get(index);
	}

	public int getStepCount() {

		return steps.size();
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	

	
	
	
}
