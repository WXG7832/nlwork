package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.Pole;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年8月7日 下午5:58:13
* 对回检板进行计量
*/
public class CalCheckCalculateData extends Data implements Configable, Queryable, Responsable {
    
	private Pole  pole = Pole.NORMAL;
	private byte  ready = 0;
	private double calculateDot;
	private WorkMode  workMode = WorkMode.NONE;
	private double adc; //单位0.01mA或mV
	
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
		data.add((byte) pole.ordinal());
		data.add((byte) workMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 100), 3, true)));
		data.add(ready);
		// ADC
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 10), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		//分区
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        //极性
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		//工作方式
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(flag >= WorkMode.values().length) {
			
			throw new RuntimeException("the workmode value is error : " + flag);
		}
		//计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;
		//ready信号
		ready = data.get(index++);
		//adc
		adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return WorkformCode.CheckCalculateCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}
	
	

}
