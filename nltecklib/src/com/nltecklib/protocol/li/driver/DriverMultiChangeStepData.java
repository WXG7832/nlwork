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
* @author  wavy_zheng
* @version 创建时间：2021年5月8日 上午11:22:48
* (11-04新增查询功能Queryable)
* 类说明
*/
public class DriverMultiChangeStepData extends Data implements Configable,Queryable,Responsable {
    
	
	private short  chnSelect; //通道选择标志位
	
	
	public static class StepControl {
		
		public int chnIndex;
		public int loopIndex;//循环号
		public int stepIndex;//当前步次
		public int rangeLevel;//档位
		public int miliseconds; //流逝时间ms
	}
	
	private List<StepControl>  stepControls = new ArrayList<>();

	
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
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnSelect, 2, true)));
		
		for(int n = 0 ; n < stepControls.size() ; n++) {
			data.add((byte) stepControls.get(n).loopIndex);
			data.add((byte) stepControls.get(n).stepIndex);
			data.add((byte) stepControls.get(n).rangeLevel);
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnSelect = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		for(int n = 0 ; n < stepControls.size() ; n++) {
			StepControl sc = new StepControl();
			sc.loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.rangeLevel = ProtocolUtil.getUnsignedByte(data.get(index++));
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.MultiTransferProcessCode;
	}

	public short getChnSelect() {
		return chnSelect;
	}

	public void setChnSelect(short chnSelect) {
		this.chnSelect = chnSelect;
	}

	public List<StepControl> getStepControls() {
		return stepControls;
	}

	public void setStepControls(List<StepControl> stepControls) {
		this.stepControls = stepControls;
	}

	
	
	

}
