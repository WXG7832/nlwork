package com.nltecklib.protocol.power.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 下午2:52:32
* 驱动板极性,界定值通信协议
*/
public class DriverPoleData extends Data implements Configable, Queryable, Responsable {
    
	private Pole pole = Pole.POSITIVE;
    private double voltageBound; // 高于该值一律判定为电池负载；否则为空载
	
	
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
		
		data.add((byte) pole.ordinal()); // 极性
		// 界定值正负，正数0，负数1
		data.add((byte) (voltageBound >= 0 ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (Math.abs(voltageBound * Math.pow(10, 1))), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int poleIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (poleIndex > Pole.values().length - 1) {

		    throw new RuntimeException("error pole index(" + poleIndex + ") in driver(" + (driverIndex + 1) + ")");
		}
		pole = Pole.values()[poleIndex];
		boolean minus = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		voltageBound = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		if (minus) {

		    voltageBound = -voltageBound;
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.PoleCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public double getVoltageBound() {
		return voltageBound;
	}

	public void setVoltageBound(double voltageBound) {
		this.voltageBound = voltageBound;
	}

	@Override
	public String toString() {
		return "DriverPoleData [pole=" + pole + ", voltageBound=" + voltageBound + "]";
	}
	
	

}
