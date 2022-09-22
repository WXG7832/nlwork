package com.nltecklib.protocol.lab.mbcal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午9:36:38
* 类说明
*/
public class MbTemperatureData extends Data implements Queryable, Responsable {
    
	private double resistanceTemperature;//电阻温度
	private double constantTemperature;//恒温区温度
	
	@Override
	public boolean supportMain() {
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
		// TODO Auto-generated method stub
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resistanceTemperature * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (constantTemperature * 10), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		resistanceTemperature = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		constantTemperature = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
	}

	public double getResistanceTemperature() {
		return resistanceTemperature;
	}

	public void setResistanceTemperature(double resistanceTemperature) {
		this.resistanceTemperature = resistanceTemperature;
	}

	public double getConstantTemperature() {
		return constantTemperature;
	}

	public void setConstantTemperature(double constantTemperature) {
		this.constantTemperature = constantTemperature;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.TEMP;
	}

	@Override
	public String toString() {
		return "MbTemperatureData [resistanceTemperature=" + resistanceTemperature + ", constantTemperature="
				+ constantTemperature + "]";
	}
	
	

}
