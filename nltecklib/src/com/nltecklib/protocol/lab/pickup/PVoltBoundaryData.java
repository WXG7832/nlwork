package com.nltecklib.protocol.lab.pickup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PVoltBoundaryData extends Data implements Configable, Queryable, Responsable {

	// private Pole pole = Pole.POSITIVE;
	private double boundary;

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
         
		data.add((byte) (boundary >= 0 ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) Math.abs(boundary) * 1000, 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		boolean pole = data.get(index++) == 0;// 0为正
		// 界定值
		boundary = (pole ? 1 : -1)
				* (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
		index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.VoltBoundaryCode;
	}

	public double getBoundary() {
		return boundary;
	}

	public void setBoundary(double boundary) {
		this.boundary = boundary;
	}

	@Override
	public String toString() {
		return "VoltBoundaryData [boundary=" + boundary + "]";
	}
	
	

}
