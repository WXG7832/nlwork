package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class LogicLabPoleData extends Data implements Configable, Responsable {

	private Pole pole = Pole.NORMAL;
	private short chnFlag = (short) 0xffff; // 全选
	private double voltageBound; // 高于该值一律判定为电池负载；否则为空载

	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public boolean supportDriver() {

		return true;
	}

	@Override
	public boolean supportChannel() {

		return false;
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnFlag), 2, true)));
		data.add((byte) pole.ordinal());

		// 界定值正负，正数0，负数1
		data.add((byte) (voltageBound >= 0 ? 0 : 1));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (Math.abs(voltageBound * 10)), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code :" + code);
		}
		pole = Pole.values()[code];
		boolean minus =  ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		voltageBound = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		if(minus) {
			
			voltageBound = -voltageBound;
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return LogicCode.LabPoleCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public short getChnFlag() {
		return chnFlag;
	}

	public void setChnFlag(short chnFlag) {
		this.chnFlag = chnFlag;
	}

	public double getVoltageBound() {
		return voltageBound;
	}

	public void setVoltageBound(double voltageBound) {
		this.voltageBound = voltageBound;
	}

	@Override
	public String toString() {
		return "LogicLabPoleData [pole=" + pole + ", chnFlag=" + chnFlag + ", voltageBound=" + voltageBound
				+ ", unitIndex=" + unitIndex + ", driverIndex=" + driverIndex + "]";
	}

	
	
	
	

}
