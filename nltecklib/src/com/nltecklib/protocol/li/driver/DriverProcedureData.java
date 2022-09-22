package com.nltecklib.protocol.li.driver;

import java.util.ArrayList;
import java.util.Arrays;
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
 * 流程配置
 * @author admin
 */
public class DriverProcedureData extends Data implements Configable, Queryable, Responsable{

    private boolean MODE_SYNC;//0:异步模式 1：同步模式
    
	private int loopCount;//0表示不循环
	
	private int loopSt;//步次号从1开始，第1步表示1
	
	private int loopEd;//循环结束步次
	
    private List<StepParam> steps;
    
    
    
    
    public DriverProcedureData() {
    	
    	steps = new ArrayList<StepParam>();
    }
    
    
    
    
	public static class StepParam{

		private  WorkMode  workMode;
		private  long    specialVoltage;
		private  long    specialCurrent;
		private  long      overTime; //时间结束值
		private  EndMode   endMode = EndMode.CONTINUE;//结束方式
	
		
		public StepParam() {}
		
		public StepParam(WorkMode  workMode,long specialVoltage,long specialCurrent,long overTime,EndMode endMode){
			this.workMode = workMode;
			this.specialVoltage = specialVoltage;
			this.specialCurrent = specialCurrent;
			this.overTime = overTime;
			this.endMode = endMode;
		}
		
		public WorkMode getWorkMode() {
			return workMode;
		}
		public void setWorkMode(WorkMode workMode) {
			this.workMode = workMode;
		}
		public long getSpecialVoltage() {
			return specialVoltage;
		}
		public void setSpecialVoltage(long specialVoltage) {
			this.specialVoltage = specialVoltage;
		}
		public long getSpecialCurrent() {
			return specialCurrent;
		}
		public void setSpecialCurrent(long specialCurrent) {
			this.specialCurrent = specialCurrent;
		}
		public long getOverTime() {
			return overTime;
		}
		public void setOverTime(long overTime) {
			this.overTime = overTime;
		}
		public EndMode getEndMode() {
			return endMode;
		}
		public void setEndMode(EndMode endMode) {
			this.endMode = endMode;
		}
	}
	
	/**结束方式*/
	public enum EndMode{
		CONTINUE,
		SLEEP,
		STOP
	}
	
	
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
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);
		//转步模式
		data.add((byte) (MODE_SYNC ? 0x01:0x00));
		//循环次数
	    data.add((byte) loopCount);
	    data.add((byte) loopSt);
	    data.add((byte) loopEd);
	    data.add((byte) steps.size());
	    
	    for(StepParam step : steps) {
	    	
		    data.add((byte) step.workMode.ordinal());
			//额定电压
			data.addAll(Arrays.asList(ProtocolUtil.split(step.specialVoltage, 2, true)));
			//额定电流
			data.addAll(Arrays.asList(ProtocolUtil.split(step.specialCurrent, 3, true)));
			//时间结束值
			data.addAll(Arrays.asList(ProtocolUtil.split(step.overTime, 3, true)));
		    //结束方式
			data.add((byte) step.endMode.ordinal());
	    }
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		MODE_SYNC = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		loopCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		loopSt = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		loopEd = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int stepCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int num = 0; num < stepCount ; num++) {
			
			StepParam stepParam = new  StepParam();
			
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > WorkMode.values().length - 1) {
				
				throw new RuntimeException("error work mode code : " + code);
			}
			stepParam.setWorkMode(WorkMode.values()[code]);
			//电压
			stepParam.setSpecialVoltage(ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true));
			index += 2;
			//电流
			stepParam.setSpecialCurrent(ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true));
			index += 3;
			//截止条件
			stepParam.setOverTime(ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true));
			index += 3;
			
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > EndMode.values().length - 1) {
				
				throw new RuntimeException("error End mode code : " + code);
			}
			stepParam.setEndMode(EndMode.values()[code]);
			
			steps.add(stepParam);
			
		}
		
		
		
		
	}

	@Override
	public Code getCode() {
		return DriverCode.ProcedureCode;
	}

	public boolean isMODE_SYNC() {
		return MODE_SYNC;
	}

	public void setMODE_SYNC(boolean MODE_SYNC) {
		this.MODE_SYNC = MODE_SYNC;
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

	public List<StepParam> getSteps() {
		return steps;
	}

	public void setSteps(List<StepParam> steps) {
		this.steps = steps;
	}

	
}
