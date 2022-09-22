package com.nltecklib.protocol.li.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
* @author  wavy_zheng
* @version 创建时间：2020年4月21日 下午2:20:11
* 类说明
*/
public class CheckHKCalibrateData extends Data implements Configable, Queryable, Responsable {
    
	private SwitchState  switchState = SwitchState.OFF;
	private List<Double> adcList  = new ArrayList<Double>();
	
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add(isReverseDriverChnIndex() ? (byte)ProtocolUtil.reverseChnIndexInLogic(chnIndex,Data.getDriverChnCount()) : (byte) chnIndex);
		data.add((byte) 1); //极性默认为正
		data.add((byte) switchState.ordinal());
		data.add((byte) adcList.size());
		for(int n = 0 ; n < adcList.size() ; n++) {
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcList.get(n) * 100), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(val,Data.getDriverChnCount()) : val;
		index++; //极性
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(flag > SwitchState.values().length - 1) {
			
			throw new RuntimeException("error switch state code " + flag);
			
		}
		switchState = SwitchState.values()[flag];
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcList.add(adc);
		}
		
		

	}

	@Override
	public Code getCode() {
		
		return CheckCode.HKCalibrateCode;
	}

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	public List<Double> getAdcList() {
		return adcList;
	}

	public void setAdcList(List<Double> adcList) {
		this.adcList = adcList;
	}
	
	

}
