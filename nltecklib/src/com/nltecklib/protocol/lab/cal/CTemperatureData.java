package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 查询校准板电阻温度
 * @author Administrator
 *
 */
public class CTemperatureData extends Data implements Queryable, Responsable {
    
	private double resistanceTemperature;//电阻温度
	private double constantTemperature;//恒温区温度
	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resistanceTemperature * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (constantTemperature * 10), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		resistanceTemperature = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		constantTemperature = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.TEMP;
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
	public String toString() {
		return "TemperatureData [resistanceTemperature=" + resistanceTemperature + ", constantTemperature="
				+ constantTemperature + "]";
	}


	

}
