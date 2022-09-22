package com.nltecklib.protocol.li.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverMultiChangeStepData.StepControl;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年5月8日 下午7:25:54
* 类说明
*/
public class DriverStartInitData extends Data implements Configable, Responsable {
   
	private List<StepControl> steps = new ArrayList<>();
	
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
		data.add((byte) steps.size());
		for(int n = 0 ; n < steps.size() ; n++) {
			
			StepControl sc = steps.get(n);
			
			data.add((byte) sc.chnIndex);
			data.addAll(Arrays.asList(ProtocolUtil.split(sc.miliseconds, 4, true)));
			data.add((byte) sc.loopIndex);
			data.add((byte) sc.stepIndex);
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			StepControl sc = new StepControl();
			sc.chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.miliseconds = (int) ProtocolUtil.compose(data.subList(index, index+4).toArray(new Byte[0]), true);
			index += 4;
			sc.loopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			sc.stepIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
			steps.add(sc);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.START_INIT_CODE;
	}

	public List<StepControl> getSteps() {
		return steps;
	}

	public void setSteps(List<StepControl> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "DriverStartInitData [steps=" + steps + "]";
	}
	
	

}
