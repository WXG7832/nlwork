package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class TrayPressureData extends Data implements Configable, Queryable, Responsable {
    
	private double minPressure;
	private double maxPressure;
	
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
	
	

	public double getMinPressure() {
		return minPressure;
	}

	public void setMinPressure(double minPressure) {
		this.minPressure = minPressure;
	}

	public double getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(double maxPressure) {
		this.maxPressure = maxPressure;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(minPressure), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(maxPressure), 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		minPressure = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		maxPressure = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.PressureCode;
	}

}
