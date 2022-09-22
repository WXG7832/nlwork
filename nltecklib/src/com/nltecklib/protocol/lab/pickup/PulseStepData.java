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
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.PulseMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月16日 下午5:20:48
* 脉冲步次
*/
public class PulseStepData extends Data implements Configable, Queryable, Responsable {
    
	private int stepIndex;
	private int loopCount;
	private List<PulseStep> pulseSteps = new ArrayList<>();
	
	
	public static class PulseStep {
		
		public PulseMode mode;
		public long      milisecs;
		
	}
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) stepIndex);
		data.add((byte) loopCount);
        data.add((byte) pulseSteps.size());
        for(int n = 0 ; n < pulseSteps.size() ; n++) {
        	
        	data.add((byte) pulseSteps.get(n).mode.ordinal());
        	data.addAll(Arrays.asList(ProtocolUtil.split((long)pulseSteps.get(n).milisecs, 3, true)));
        }
	}

	@Override
	public void decode(List<Byte> encodeData) {
	
		data = encodeData;
		int index = 0;
		stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		loopCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		int pulseCount =  ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < pulseCount ; n++) {
			
			PulseStep  ps = new PulseStep();
			
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code > PulseMode.values().length - 1) {
				
				throw new RuntimeException("error pulse mode code:" + code);
			}
			
			ps.mode = PulseMode.values()[code];
			ps.milisecs = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.PulseCode;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	

	public List<PulseStep> getPulseSteps() {
		return pulseSteps;
	}

	public void setPulseSteps(List<PulseStep> pulseSteps) {
		this.pulseSteps = pulseSteps;
	}

	@Override
	public String toString() {
		return "PulseStepData [stepIndex=" + stepIndex + ", loopCount=" + loopCount + ", pulseSteps=" + pulseSteps
				+ "]";
	}

	
	
	

}
