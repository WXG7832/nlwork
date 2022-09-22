package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



public class CalBaseVoltageData extends Data implements Configable,Queryable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private double voltBase = 0; //单位mV

	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex); //校准板序号
		data.add((byte) chnIndex); //0-15
		data.add((byte) workState.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltBase * 10), 2, true)));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > WorkState.values().length - 1) {

			throw new RuntimeException("error work state index : " + pos);
		}
		workState = WorkState.values()[pos];
		voltBase = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;

	}

	@Override
	public Code getCode() {

		return WorkformCode.CalBaseVoltCode;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	public WorkState getWorkState() {
		return workState;
	}

	public void setWorkState(WorkState workState) {
		this.workState = workState;
	}

	public double getVoltBase() {
		return voltBase;
	}

	public void setVoltBase(double voltBase) {
		this.voltBase = voltBase;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

}
