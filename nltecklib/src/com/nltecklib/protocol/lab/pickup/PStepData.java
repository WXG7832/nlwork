package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 采集板流程步次配置查询命令
 * 
 * @author Administrator
 *
 */
public class PStepData extends Data implements Configable, Queryable, Responsable {

	public static class Step {

		public int stepIndex;
		public int loopIndex;
		public WorkMode workMode;
		public double voltage;
		public double current;
		public double endVoltage; // 截止电压值
		public double endCurrent; // 截止电流值
		public long miliseconds; // 截止时间，单位ms
		public double endCapacity;// 容量结束值
		public double startCapacity;// 容量初始值
		public long stepElapseTime; // 步次已流逝时间，兼容旧的
		public byte skipFlag; // 转步标志,第1位表示电压，第2位电流，第3位时间，第4位容量

		@Override
		public String toString() {
			return "Step [stepIndex=" + stepIndex + ", loopIndex=" + loopIndex + ", workMode=" + workMode + ", voltage="
					+ voltage + ", current=" + current + ", endVoltage=" + endVoltage + ", endCurrent=" + endCurrent
					+ ", miliseconds=" + miliseconds + ", endCapacity=" + endCapacity + ", startCapacity="
					+ startCapacity + ", stepElapseTime=" + stepElapseTime + ", skipFlag=" + skipFlag + "]";
		}

	}

	private List<Step> steps = new ArrayList<Step>();

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) steps.size());
		for (int n = 0; n < steps.size(); n++) {

			Step step = steps.get(n);
			// 步次号
			data.addAll(Arrays.asList(ProtocolUtil.split(step.stepIndex, 2, true)));
			// 循环号
			data.addAll(Arrays.asList(ProtocolUtil.split(step.loopIndex, 2, true)));
			// 工作方式
			data.add((byte) step.workMode.ordinal());
			// 电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.voltage * 1000), Data.getGeneration() == Generation.TH1 ? 3 : 4, true)));
			// 电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.current * 1000), Data.getGeneration() == Generation.TH1 ? 3 : 4, true)));
			// 截止电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.endVoltage * 1000), Data.getGeneration() == Generation.TH1 ? 3 : 4, true)));
			// 截止电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.endCurrent * 1000), Data.getGeneration() == Generation.TH1 ? 3 : 4, true)));
			// 截止时间
			data.addAll(Arrays.asList(ProtocolUtil.split(step.miliseconds, 6, true)));

			if (Data.isUsePickupCapacity()) {
				// 容量结束值
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.endCapacity * Math.pow(10, 1)), 4, true)));

				// 容量起始值
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.startCapacity * Math.pow(10, 1)), 4, true)));

			} else {

				// 步次流逝时间
				data.addAll(Arrays.asList(ProtocolUtil.split(step.stepElapseTime, 6, true)));

			}
			// 流程步次转步标志
			if (Data.isUseAndStepCondition()) {

				data.add(step.skipFlag);
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
        
		/**
		 * 电压电流字节数
		 */
		int vaLen = Data.getGeneration() == Generation.TH1 ? 3 : 4;
		
		
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			Step step = new Step();
			// 步次号
			step.stepIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			// 循环号
			step.loopIndex = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			// 工作方式
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > WorkMode.values().length - 1) {

				throw new RuntimeException("error work mode code :" + code);
			}
			step.workMode = WorkMode.values()[code];
			// 额定电压
			step.voltage = (double) ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true)
					/ 1000;
			index += vaLen;
			// 额定电流
			step.current = (double) ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true)
					/ 1000;
			index += vaLen;
			// 截止电压
			step.endVoltage = (double) ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true)
					/ 1000;
			index += vaLen;
			// 截止电流
			step.endCurrent = (double) ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true)
					/ 1000;
			index += vaLen;
			// 截止时间
			step.miliseconds = ProtocolUtil.compose(data.subList(index, index + 6).toArray(new Byte[0]), true);
			index += 6;

			if (Data.isUsePickupCapacity()) {
				// 结束容量
				step.endCapacity = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 10;
				index += 4;
				// 开始容量
				step.startCapacity = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 10;
				index += 4;
			} else {
               
				step.stepElapseTime = ProtocolUtil.compose(data.subList(index, index + 6).toArray(new Byte[0]), true);
				index += 6;
			}
			// 流程步次转步标志
			if (Data.isUseAndStepCondition()) {

				step.skipFlag = data.get(index++);
			}

			steps.add(step);
		}
	}

	@Override
	public Code getCode() {

		return ChipPickupCode.StepCode;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "StepData [steps=" + steps + "]";
	}

}
