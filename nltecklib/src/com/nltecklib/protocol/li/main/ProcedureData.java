package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ProcedureData extends Data implements Configable,Queryable,Responsable , Cloneable{
	
    
	public static class Step implements Cloneable{
		
		private int      id;   
		public  int      stepIndex; //步次序号
		public  int      loopIndex; //循环号
		public WorkMode  workMode = WorkMode.SLEEP;
		public double    specialVoltage;       //恒定/额定电压
		public double    specialCurrent;      //恒定/额定电流
		public double    overThreshold;     //结束电压/电流/时间
		public double    overCapacity;       //结束容量
		public int       overTime;          //结束步次时间
		public double    deltaVoltage;      //结束压差
		public OverMode  overMode = OverMode.PROCEED;         //步次结束模式
		public int       pressure;         //压力值
		public boolean   pressureChanged;  //压力值是否已经变更
		public boolean   timeProtect;     //是否启用步次时间保护
		
		/** 2022/02/13 power60A添加 记录阀值时间 */
		public int       pressureVariation; //增量
		public int       recordTime;      //记录时间阈值
		public int       varWaitTime; //增量停留时间
		
		/**
		 * 工步保护
		 */
		public Data     protection; //相对应的工步保护
		
	


		@Override
		public Object clone() throws CloneNotSupportedException {
			
			Step step =  (Step)super.clone();
			if(this.protection != null) {
			    step.protection = (Data) this.protection.clone();
			}
			
			return step;
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

		public double getOverThreshold() {
			return overThreshold;
		}

		public void setOverThreshold(double overThreshold) {
			this.overThreshold = overThreshold;
		}

		public double getOverCapacity() {
			return overCapacity;
		}

		public void setOverCapacity(double overCapacity) {
			this.overCapacity = overCapacity;
		}

		public int getOverTime() {
			return overTime;
		}

		public void setOverTime(int overTime) {
			this.overTime = overTime;
		}

		public double getDeltaVoltage() {
			return deltaVoltage;
		}

		public void setDeltaVoltage(double deltaVoltage) {
			this.deltaVoltage = deltaVoltage;
		}

		public OverMode getOverMode() {
			return overMode;
		}

		public void setOverMode(OverMode overMode) {
			this.overMode = overMode;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
		
		public int getLoopIndex() {
			return loopIndex;
		}

		public void setLoopIndex(int loopIndex) {
			this.loopIndex = loopIndex;
		}

		public int getStepIndex() {
			return stepIndex;
		}

		public void setStepIndex(int stepIndex) {
			this.stepIndex = stepIndex;
		}

		public int getPressure() {
			return pressure;
		}

		public void setPressure(int pressure) {
			this.pressure = pressure;
		}
		
		public int getPressureVariation() {
			return pressureVariation;
		}

		public void setPressureVariation(int pressureVariation) {
			this.pressureVariation = pressureVariation;
		}

		public boolean isTimeProtect() {
			return timeProtect;
		}

		public void setTimeProtect(boolean timeProtect) {
			this.timeProtect = timeProtect;
		}
		

		public int getRecordTime() {
			return recordTime;
		}

		public void setRecordTime(int recordTime) {
			this.recordTime = recordTime;
		}

		public int getVarWaitTime() {
			return varWaitTime;
		}

		public void setVarWaitTime(int varWaitTime) {
			this.varWaitTime = varWaitTime;
		}

		@Override
		public String toString() {
			return "Step [id=" + id + ", stepIndex=" + stepIndex + ", loopIndex=" + loopIndex + ", workMode=" + workMode
					+ ", specialVoltage=" + specialVoltage + ", specialCurrent=" + specialCurrent + ", overThreshold="
					+ overThreshold + ", overCapacity=" + overCapacity + ", overTime=" + overTime + ", deltaVoltage="
					+ deltaVoltage + ", overMode=" + overMode + ", pressure=" + pressure + ", pressureChanged="
					+ pressureChanged + ", timeProtect=" + timeProtect + ", pressureVariation=" + pressureVariation
					+ ", recordTime=" + recordTime + ", varWaitTime=" + varWaitTime + "]";
		}

		
		
	}
	private static final int  NAME_BYTES = 50 ; //固定50个字节
	
	private StepMode  stepMode = StepMode.ASYNC;
	private int  loopCount; //0无效
	private int  loopSt;
	private int  loopEd;
	/**
	 * 流程工序
	 */
	public WorkType  workType = WorkType.AG; //默认为AG工序
	private List<Step> steps = new ArrayList<Step>();
	private String name = "";
	
	private static final int  STEP_BYTES = 22;

	@Override
	public void encode() {
        
		data.add((byte)unitIndex);
		//流程名
		byte[] array = null;
		try {
			array = name.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		for(int i = 0 ; i < NAME_BYTES ; i++) {
			
			  if(i < array.length) {
				  
				  data.add(array[i]);
			  }else {
				  
				  data.add((byte) 0);
			  }
		}
		data.add((byte) stepMode.ordinal());
		data.add((byte)loopCount);
		data.add((byte)loopSt);
		data.add((byte)loopEd);
		/**
		 * 流程工序
		 */
		if(isUseProcedureWorkType()) {
			data.add((byte) workType.ordinal());
		}
		data.add((byte)steps.size());

		for (int i = 0; i < steps.size(); i++) {

			Step step = steps.get(i);
			// 工作方式
			data.add((byte) step.workMode.ordinal());
			// 额定恒定电压
			
		    data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.specialVoltage * 10) , 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.specialCurrent * 10), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.overThreshold * 10 ), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(step.overTime, 3, true)));
			data.add((byte) (step.timeProtect ? 1 : 0));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.deltaVoltage * 10 ), 2, true)));
			
			//容量结束条件
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.overCapacity * 10 ), 4, true)));
			//设定压力值
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.pressure), 2, true)));
			data.add((byte) step.overMode.ordinal());
			

			
			/** 2022/02/21 power60A添加 记录阀值时间、增量 */
			if(isUseMainStepRecordTime()) {
				data.addAll(Arrays.asList(ProtocolUtil.split(step.recordTime, 3, true)));
			}
			if(isUseMainStepVariable()) {
				data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.pressureVariation), 2, true)));
			}	
			if(isUseMainStepVarWaitTime()) {
				data.addAll(Arrays.asList(ProtocolUtil.split((long)(step.varWaitTime), 2, true)));
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		long val  = 0;
        data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		//流程名
		byte[] nameBytes = new byte[NAME_BYTES];
		int strLen = 0;
        for(int i = index ; i < index + NAME_BYTES ; i++) {
        	
        	if(data.get(i) == 0) {
        		
        		break;
        	}
        	strLen++;
        	nameBytes[i - index] = data.get(i);
        }
        try {
        	
			name = new String(Arrays.copyOfRange(nameBytes, 0, strLen),"utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
        index += NAME_BYTES;
        int code = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
        if(code > StepMode.values().length - 1) {
        	
        	throw new RuntimeException("error step mode code:" +  code);
        }
        
        stepMode = StepMode.values()[code];
		loopCount = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopSt    = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		loopEd    = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		/**
		 * 流程工序类型
		 * 
		 */
		if(isUseProcedureWorkType()) {
			val = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(val > WorkType.values().length - 1) {
				
				throw new RuntimeException("error procedure work type code:" + val);
			}
			workType = WorkType.values()[(int) val];
		}
		
		
		int stepCount = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

		/** 20220221 新增增量和记录阀值 ，协议长度变化 */
		int stepBytes = STEP_BYTES;
		if(isUseMainStepRecordTime()) {
			stepBytes += 3;
		}
		if(isUseMainStepVariable()) {
			stepBytes += 2;
		}
		if(isUseMainStepVarWaitTime()) {
			stepBytes += 2;
		}
		if(isUseProcedureWorkType()) {
			stepBytes += 1;
		}
		
		// 解码步次信息，每个步次占22个字节
//		if ((encodeData.size() - index) % stepBytes != 0) {
//
//			throw new RuntimeException("error step byte :" + (encodeData.size() - index));
//		}
//		if (stepCount != (encodeData.size() - index) / stepBytes) {
//
//			throw new RuntimeException("invalid stepCount: " + ((encodeData.size() - index) / stepBytes));
//		}

		for (int i = 0; i < stepCount; i++) {

			Step step = new Step();
			
			step.stepIndex = i + 1;
			step.workMode = WorkMode.values()[encodeData.get(index++)]; // 工作方式
			val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
			step.specialVoltage = (double) val / 10;
			index += 2;
			//额定电流-恒定电流
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
			
			val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
			step.pressure = (int) val ;
			index += 2;

			step.overMode = OverMode.values()[encodeData.get(index++)];
			
			
			
			/** 2022/02/21 LLC power60A 添加 记录阀值时间、增量 */
			if(isUseMainStepRecordTime()) {
				val = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
				step.recordTime = (int) val;
				index += 3;
			}
			if(isUseMainStepVariable()) {
				val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
				step.pressureVariation = (int) val;
				index += 2;		
			}
			if(isUseMainStepVarWaitTime()) {
				val = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
				step.varWaitTime = (int) val;
				index += 2;		
			}
			steps.add(step);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ProcedureCode;
	}

	
	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	

	@Override
	public String toString() {
		return "ProcedureData [loopCount=" + loopCount + ", loopSt=" + loopSt + ", loopEd=" + loopEd + ", steps="
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
	
	public void addSteps(List<Step> steps) {
		
		this.steps.addAll(steps);
	}

	public List<Step> getSteps() {
		return steps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StepMode getStepMode() {
		return stepMode;
	}

	public void setStepMode(StepMode stepMode) {
		this.stepMode = stepMode;
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

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	
	
	
	
}
