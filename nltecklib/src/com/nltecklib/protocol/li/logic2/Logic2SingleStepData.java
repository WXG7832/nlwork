package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.OptMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年6月23日 上午10:20:26
*  主控在单步模式下，通过此条协议进行对逻辑板进行单步启动控制
*/
public class Logic2SingleStepData extends Data implements Configable, Queryable, Responsable {
    
	private SwitchState switchState = SwitchState.OFF; //通道开还是关?
	private OptMode    optMode = OptMode.SYNC;
	private List<Byte> chnIndexList = new ArrayList<>();
	private Step       step = new Step();
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) optMode.ordinal());
		// 操作
		data.add((byte) switchState.ordinal());
        data.add((byte) chnIndexList.size());
       
        if(Data.isReverseDriverChnIndex()) {
        	
        	 List<Byte> list = new ArrayList<>();
        	for(Byte chnIndex : chnIndexList) {
        		
        		int index = ProtocolUtil.getUnsignedByte(chnIndex);
        		list.add((byte) ProtocolUtil.reverseChnIndexInLogic(index, Data.getDriverChnCount()));
        	}
        	chnIndexList = list;
        }
        data.addAll(chnIndexList);
        //当前执行步次
        byte modeCode = -1;
        if(step.workMode == WorkMode.CCC) {
        	
        	modeCode = 0;
        } else if(step.workMode == WorkMode.CC_CV) {
        	
        	modeCode = 1;
        } else if(step.workMode == WorkMode.CCD) {
        	
        	modeCode = 2;
        }
        data.add((byte)modeCode);
        data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialVoltage * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.specialCurrent * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (step.overThreshold * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(step.overTime, 3, true)));
        

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		int val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (val > OptMode.values().length - 1) {

			throw new RuntimeException("error opt mode index:" + val );
		}
		optMode = OptMode.values()[val];
	
		val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		if (val > SwitchState.values().length - 1) {

			throw new RuntimeException("error switch state index:" + val );
		}
		switchState = SwitchState.values()[val];
		
		int count = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		
		chnIndexList = encodeData.subList(index, index + count);
		
        if(Data.isReverseDriverChnIndex()) {
        	
        	List<Byte> list = new ArrayList<>();
        	for(Byte chnIndex : chnIndexList) {
        		
        		int unIndex = ProtocolUtil.getUnsignedByte(chnIndex);
        		list.add((byte) ProtocolUtil.reverseChnIndexInLogic(unIndex, Data.getDriverChnCount()));
        	}
        	chnIndexList = list;
        }
        //
        int modeIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
        if(modeIndex == 0) {
        	
        	step.workMode = WorkMode.CCC;
        } else if(modeIndex == 1) {
        	
        	step.workMode = WorkMode.CC_CV;
        } else if(modeIndex == 2) {
        	
        	step.workMode = WorkMode.CCD;
        } else {
        	
        	throw new RuntimeException("error work mode code:" + modeIndex);
        }
        
        long value = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
		step.specialVoltage = (double) val / 10;
		index += 2;
		// 额定电流-恒定电流
		value = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
		step.specialCurrent = (double) val / 10;
		index += 3;

		value = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
		step.overThreshold = (double) val / 10;
		index += 3;

		value = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true);
		step.overTime = (int) val;
		index += 3;
        

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.SingleStepCode;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	public OptMode getOptMode() {
		return optMode;
	}

	public void setOptMode(OptMode optMode) {
		this.optMode = optMode;
	}

	public List<Byte> getChnIndexList() {
		return chnIndexList;
	}

	public void setChnIndexList(List<Byte> chnIndexList) {
		this.chnIndexList = chnIndexList;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
	
	

}
