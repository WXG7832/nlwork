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
 * 接触保护
 * @author Administrator
 *
 */
public class TouchProtectData extends Data implements Configable,Queryable,Responsable,Cloneable{

	private double maxTouchResi; //最大接触电阻
	private double maxVoltDiff; //最大压差
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(maxTouchResi * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(maxVoltDiff * 10), 2,true)));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		
		maxTouchResi = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		maxVoltDiff = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
	}
	
	@Override
	public Code getCode() {
		
		return MainCode.TouchProtectCode;
	}

	public double getMaxTouchResi() {
		return maxTouchResi;
	}

	public void setMaxTouchResi(double maxTouchResi) {
		this.maxTouchResi = maxTouchResi;
	}

	public double getMaxVoltDiff() {
		return maxVoltDiff;
	}

	public void setMaxVoltDiff(double maxVoltDiff) {
		this.maxVoltDiff = maxVoltDiff;
	}


	@Override
	public String toString() {
		return "TouchProtectData [maxTouchResi=" + maxTouchResi + ", maxVoltDiff=" + maxVoltDiff + "]";
	}

	@Override
	public boolean supportChannel() {
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
