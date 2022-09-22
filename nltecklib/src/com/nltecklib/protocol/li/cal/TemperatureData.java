package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 查询校准板电阻温度
 * @author Administrator
 *
 */
public class TemperatureData extends Data implements Queryable, Responsable {
    
	private double temperature;
	
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
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (temperature * 10), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		temperature = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.TEMP;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	

}
