package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.lab.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CVoltageBaseData extends Data implements Configable, Queryable, Responsable {

	private WorkState workState = WorkState.UNWORK;
	private double voltBase = 0; // µ¥Î»mV

	public CVoltageBaseData() {

	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void encode() {

		data.add((byte) workState.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltBase * 10), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int pos = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (pos > WorkState.values().length - 1) {

			throw new RuntimeException("error work state index : " + pos);
		}
		workState = WorkState.values()[pos];
		voltBase = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;

	}

	@Override
	public Code getCode() {

		return CalCode.VOLT_BASE;
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
	@Override
	public String toString() {
		return "VoltageBaseData [workState=" + workState + ", voltBase=" + voltBase + "]";
	}

	
}
