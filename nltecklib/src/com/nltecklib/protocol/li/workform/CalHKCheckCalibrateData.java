package com.nltecklib.protocol.li.workform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
* @author  wavy_zheng
* @version 创建时间：2020年4月23日 上午11:23:53
* 类说明
*/
public class CalHKCheckCalibrateData extends Data implements Configable, Queryable, Responsable {
    
	private List<Double> adcList  = new ArrayList<Double>();
	private boolean  ready;
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		data.add((byte) (ready ? 0x01 : 0x00));
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
		chnIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		ready = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcList.add(adc);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return WorkformCode.HKCheckCalibrateCode;
	}

	public List<Double> getAdcList() {
		return adcList;
	}

	public void setAdcList(List<Double> adcList) {
		this.adcList = adcList;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	

}
