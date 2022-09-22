package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 通道基准电压
 * @author Administrator
 *
 */
public class CalChnBaseVoltageData extends Data implements Configable,Queryable,Responsable{

	public enum MatchState{
		MATCHED, NORMAL
	}
	
	private boolean match=false;
	private MatchState  matchState = MatchState.NORMAL;
	private double   adc;
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) matchState.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 1000), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= MatchState.values().length) {

			throw new RuntimeException("the match state value is error:" + flag);
		}
		matchState = MatchState.values()[flag];
		//ADC
		adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
		index += 3;
	}

	@Override
	public Code getCode() {
		return MainCode.CalChnBaseVoltageCode;
	}

	@Override
	public String toString() {
		return "CalChnBaseVoltageData [match=" + match + ", matchState=" + matchState + ", adc=" + adc + "]";
	}


	public boolean isMatch() {
		return match;
	}

	public void setMatch(boolean match) {
		this.match = match;
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
