package com.nltecklib.protocol.lab.backup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BDefineVoltData extends Data implements Configable, Queryable, Responsable {

	private double defineVoltage;

	@Override
	public boolean supportMain() {
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
		// TODO Auto-generated method stub
		data.add((byte) (defineVoltage >= 0 ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (Math.abs(defineVoltage) * 1000), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;

		boolean pole = data.get(index++) == 0;

		defineVoltage = (pole ? 1 : -1)
				* (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
		index+=3;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.DefineVoltCode;
	}

	public double getDefineVoltage() {
		return defineVoltage;
	}

	public void setDefineVoltage(double defineVoltage) {
		this.defineVoltage = defineVoltage;
	}

	@Override
	public String toString() {
		return "DefineVoltData [defineVoltage=" + defineVoltage + "]";
	}

}
