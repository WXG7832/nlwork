package com.nltecklib.protocol.li.calTools.check;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.PickWork;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsCheckCalculateData extends Data implements Configable,Queryable, Responsable{


	private PickWork work = PickWork.NONE;    //工作模式
	private List<Double> adcs = new ArrayList<Double>();  //电压检测值n(只读)
	

	public PickWork getWork() {
		return work;
	}

	public void setWork(PickWork work) {
		this.work = work;
	}
	
	public List<Double> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<Double> adcs) {
		this.adcs = adcs;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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
		
		data.add((byte) chnIndex);
		
		//工作模式
		data.add((byte) work.ordinal());
		
		//电压ADC采集个数
		data.add((byte) adcs.size());
		


	
	}
	

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		/** 工作模式  */
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > PickWork.values().length - 1) {

			throw new RuntimeException("error pickwork code :" + code);
		}
		work = PickWork.values()[code];
		
		/** 电压ADC采集个数  */
		int adcCount = ProtocolUtil.getUnsignedByte(data.get(index++));

			
		for(int i = 0; i < adcCount; i++) {
			double adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcs.add(adc);
		}	
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsCheckCode.CalculateCode;
	}

	@Override
	public String toString() {
		return "CalToolsCheckCalculateData [work=" + work + ", adcs=" + adcs + "]";
	}

	
	
}
