package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月15日 下午3:32:44 类说明
 */
public class DriverStepData extends Data implements Configable, Queryable, Responsable {
   
	
	private int chnFlag; //通道选择标记
	private int loopCount; // 0无效
	private int loopSt;
	private int loopEd;
	private List<Step> steps = new ArrayList<Step>();
	
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

	@Override
	public void encode() {
         
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnFlag, Data.isUseHugeDriverChnCount() ? 4 : 2, true)));
		data.add((byte) loopCount);
		data.add((byte) loopSt);
		data.add((byte) loopEd);
		data.add((byte) steps.size());
		for (Step step:steps) {

			// 工作方式
			data.add((byte) step.workMode.ordinal());

			data.add((byte) step.loopIndex);
			data.add((byte) step.stepIndex);

			// 额定恒定电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialVoltage * 10), 2, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialCurrent * 10), 3, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.overThreshold * 10), 3, true)));

			data.addAll(Arrays.asList(ProtocolUtil.split(step.overTime, 3, true)));
			
			//容量
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.overCapacity * 10), 4, true)));
			//delta v
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.deltaVoltage * 10), 2, true)));

			data.add((byte) (step.timeProtect ? 1 : 0));

			data.add((byte) step.overMode.ordinal());
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		chnFlag = (int)ProtocolUtil.compose(encodeData.subList(index, index + (Data.isUseHugeDriverChnCount() ? 4 : 2)).toArray(new Byte[0]), true);
		index += (Data.isUseHugeDriverChnCount() ? 4 : 2);
		
		loopCount = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopSt = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopEd = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		steps.clear();
		for (int n = 0; n < count; n++) {

			Step step = new Step();
			long val = 0;
			step.workMode = WorkMode.values()[encodeData.get(index++)]; // 工作方式
			step.loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			step.stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

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
			
			val = ProtocolUtil.compose(encodeData.subList(index, index + 4).toArray(new Byte[0]), true);
			step.overCapacity = (double) val / 10;
			index += 4;
			
			val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
			step.deltaVoltage = (double) val / 10;
			index += 2;

			val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
			step.timeProtect = val == 1;
			
			step.overMode = OverMode.values()[encodeData.get(index++)];

			steps.add(step);

		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.StepCode;
	}

	public void appendStep(Step step) {

		steps.add(step);
	}

	public List<Step> getSteps() {
		return steps;
	}

	@Override
	public String toString() {
		return "DriverStepData [chnFlag=" + chnFlag + ", loopCount=" + loopCount + ", loopSt=" + loopSt + ", loopEd="
				+ loopEd + ", steps=" + steps + "]";
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

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public int getChnFlag() {
		return chnFlag;
	}

	public void setChnFlag(int chnFlag) {
		this.chnFlag = chnFlag;
	}
	
	
	
}
