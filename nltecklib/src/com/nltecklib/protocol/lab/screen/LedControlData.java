package com.nltecklib.protocol.lab.screen;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.Led;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午7:02:38
* LED灯控制
*/
public class LedControlData extends Data implements Configable, Queryable, Responsable {
   
	private Led   led = Led.OFF;
	
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
		
		data.add((byte) led.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int code = ProtocolUtil.getUnsignedByte(data.get(0));
		if(code > Led.values().length - 1) {
			
			throw new RuntimeException("error led code : " + code);
		}
		led = Led.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.LEDCode;
	}

	public Led getLed() {
		return led;
	}

	public void setLed(Led led) {
		this.led = led;
	}

	@Override
	public String toString() {
		return "LedControlData [led=" + led + "]";
	}
	
	

}
