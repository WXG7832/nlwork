package com.nltecklib.protocol.li.check;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 回检板主要校准通信协议
 * @author Administrator
 *
 */
public class CheckCalProcessData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private byte ready = 0;
	private double  adc;
	
	
	public CheckCalProcessData() {
		
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
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

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		if(isReverseDriverChnIndex()) {
			
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}
		
		data.add((byte) chnIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) ready);
		// adc最终值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 10), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(isReverseDriverChnIndex()) {
			
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
					
		}

		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		// ready
		ready = data.get(index++);
		// adc
		adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

	}

	@Override
	public Code getCode() {

		return CheckCode.ChnCalCode;
	}

}
