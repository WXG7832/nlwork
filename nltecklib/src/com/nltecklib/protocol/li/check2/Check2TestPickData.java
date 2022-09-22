package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Check2TestPickData extends Data implements Queryable, Responsable {

	private List<Double> backVolts = new ArrayList<Double>();
	private List<Double> powerVolts = new ArrayList<Double>();

	@Override
	public boolean supportUnit() {

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

	public List<Double> getBackVolts() {
		return backVolts;
	}

	public void setBackVolts(List<Double> backVolts) {
		this.backVolts = backVolts;
	}

	public List<Double> getPowerVolts() {
		return powerVolts;
	}

	public void setPowerVolts(List<Double> powerVolts) {
		this.powerVolts = powerVolts;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		data.add((byte) backVolts.size());
		for (double v : backVolts) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (v * Math.pow(10, 2)), 3, true)));
		}

		data.add((byte) powerVolts.size());
		for (double v : powerVolts) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (v * Math.pow(10, 2)), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			double v = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, 2);
			index += 3;
			backVolts.add(v);
		}

		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			double v = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, 2);
			index += 3;
			powerVolts.add(v);
		}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.TestPickCode;
	}

}
