package com.nltecklib.protocol.li.driver.curr200;

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
 * 多模片流程配置 2A
 * @author Administrator
 */
public class Driver200aProcedureData extends Data implements Configable, Queryable, Responsable{

	private int loopCount;//0表示不循环
	private int loopSt;//步次号从1开始，第1步表示1
	private int loopEd;//循环结束步次
	
    private List<StepParam200a> steps;
    
    
    public Driver200aProcedureData() {
    	
    	steps = new ArrayList<StepParam200a>();
    }
    
    
	public static class StepParam200a{

		private  WorkMode  workMode;
		private  double    specialVoltage;
		private  double    specialCurrent;//膜片的额定电流
		
		private  long      overTime; //时间结束值
		private  EndMode_200a   endMode = EndMode_200a.CONTINUE;//结束方式
	
		
		public StepParam200a() {}
		
		public StepParam200a(WorkMode workMode, double specialVoltage, double specialCurrent, long overTime,
				EndMode_200a endMode) {
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

		public long getOverTime() {
			return overTime;
		}
		public void setOverTime(long overTime) {
			this.overTime = overTime;
		}
		public EndMode_200a getEndMode() {
			return endMode;
		}
		public void setEndMode(EndMode_200a endMode) {
			this.endMode = endMode;
		}
	}
	
	/**结束方式*/
	public enum EndMode_200a{
		CONTINUE,
		SLEEP,
		STOP
	}
	
	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);
		
		//转步模式
		//data.add((byte) (MODE_SYNC ? 0x01:0x00));
		
		//循环次数
	    data.add((byte) loopCount);
	    data.add((byte) loopSt);
	    data.add((byte) loopEd);
	    data.add((byte) steps.size());
	    
	    for(StepParam200a step : steps) {
	    	
		    data.add((byte) step.workMode.ordinal());
			//额定电压
		    data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialVoltage * 10), 2, true)));
			//膜片额定电流
		    data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialCurrent * 10), 3, true)));
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
		
		//MODE_SYNC = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		loopCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		loopSt = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		loopEd = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int stepCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int num = 0; num < stepCount ; num++) {
			
			StepParam200a stepParam = new StepParam200a();
			
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > WorkMode.values().length - 1) {
				
				throw new RuntimeException("error work mode code : " + code);
			}
			stepParam.setWorkMode(WorkMode.values()[code]);
			
			//膜片电压
			stepParam.setSpecialVoltage((double) (ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10));
			index += 2;
			
			//膜片电流
			stepParam.setSpecialCurrent((double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10));
			index += 3;
			
			//截止条件
			stepParam.setOverTime(ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true));
			index += 3;
			
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > EndMode_200a.values().length - 1) {
				
				throw new RuntimeException("error End mode code : " + code);
			}
			stepParam.setEndMode(EndMode_200a.values()[code]);
			
			steps.add(stepParam);
			
		}
		
	}

	@Override
	public Code getCode() {
		return DriverCode.Driver200aProcedureCode;
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

	public List<StepParam200a> getSteps() {
		return steps;
	}

	public void setSteps(List<StepParam200a> steps) {
		this.steps = steps;
	}
	
	public int bitcount(int n)
    {
        int count=0 ;
        while (n!=0) {
            count++ ;
            n &= (n - 1) ;
        }
        return count ;
    }

	
}
