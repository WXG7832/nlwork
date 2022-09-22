package com.nltecklib.protocol.li.check2;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;


public class Check2PoleData extends Data implements Configable, Queryable, Responsable {

	private Pole pole;
	private double boundVoltage; // 极性电压界定值

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
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) (boundVoltage < 0 ? 1 : 0));
		data.addAll(Arrays.asList(ProtocolUtil.split(Math.abs((int) (boundVoltage * 10)), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
	    data = encodeData;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if(encodeData.get(index) > Pole.values().length - 1) {
	    	//强行配置为正极性
	    	pole = Pole.NORMAL;
	    }else {
		    pole = Pole.values()[encodeData.get(index++)];
	    }
		boolean minus = encodeData.get(index++) == 1;
		boundVoltage  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		if(minus)
			boundVoltage = -boundVoltage;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.PoleCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public double getBoundVoltage() {
		return boundVoltage;
	}

	public void setBoundVoltage(double boundVoltage) {
		this.boundVoltage = boundVoltage;
	}

}
