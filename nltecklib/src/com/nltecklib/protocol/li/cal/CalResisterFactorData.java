package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;



public class CalResisterFactorData extends Data implements Queryable, Responsable {

//	private final static int EXP = 8; // 小数位精度
	private double highResisterFactor;
	private double resisterFactor;

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
		if (isDoubleResolutionSupport()) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (highResisterFactor * Math.pow(10, getResistanceResolution())), 4, true)));
		}
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resisterFactor * Math.pow(10, getResistanceResolution())), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isDoubleResolutionSupport()) {
			highResisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, getResistanceResolution());
			index += 4;
		}
		resisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, getResistanceResolution());
		index += 4;
	}

	@Override
	public Code getCode() {

		return CalCode.RESISTER;
	}

	public double getHighResisterFactor() {
		return highResisterFactor;
	}

	public double getResisterFactor() {
		return resisterFactor;
	}
	
	

}
