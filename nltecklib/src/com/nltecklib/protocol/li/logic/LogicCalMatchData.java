package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * Âß¼­°åµçÆ½Æ¥Åä
 * @author Administrator
 *
 */
public class LogicCalMatchData extends Data implements Configable, Queryable, Responsable {
   
	private Pole pole = Pole.NORMAL;
	private MatchState  matchState = MatchState.NORMAL;
	private double   adc;
	
	
	public LogicCalMatchData() {
		
		
	}
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public boolean supportDriver() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {
		
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) (isReverseDriverChnIndex() ? 
				ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount()): chnIndex));
		data.add((byte) pole.ordinal());
		data.add((byte) matchState.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 100), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount()) : val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= MatchState.values().length) {

			throw new RuntimeException("the match state value is error:" + flag);
		}
		matchState = MatchState.values()[flag];
		//ADC
		adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;
		
	}

	@Override
	public Code getCode() {
		
		return LogicEnvironment.LogicCode.MatchCalCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public MatchState getMatchState() {
		return matchState;
	}

	public void setMatchState(MatchState matchState) {
		this.matchState = matchState;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}
	
	

}
