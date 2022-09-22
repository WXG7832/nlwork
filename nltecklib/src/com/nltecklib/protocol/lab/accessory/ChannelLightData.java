package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;

/**
 * 通道指示灯
 * @author Administrator
 *
 */
public class ChannelLightData extends Data implements Queryable,Configable,Responsable {
    
	
	public enum LightColor {
		
		RED , YELLOW ,GREEN , WHITE , OFF
	}
	
	private boolean twinkle; //是否闪烁
	private LightColor lightColor = LightColor.OFF;
	
	
	@Override
	public void encode() {
        
		byte b = 0;
		switch(lightColor) {
		case RED:
			b = (byte) (b | 0x01);
			break;
		case GREEN:
			b = (byte)(b | 0x02);
			break;
		case YELLOW:
			b = (byte)(b | 0x04);
			break;
		case WHITE:
			b = (byte)(b | 0x10);
			break;
		default:
			break;
		
		}
		if(twinkle) {
			
			b = (byte) (b | 0x08);
		}
		
		data.add(b);
		

	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		byte b = data.get(index++);
		
		if((b & 0x01) > 0 ) {
			
			lightColor = LightColor.RED;
		} else if((b & 0x02) > 0 ){
			
			lightColor = LightColor.GREEN;
		} else if((b & 0x04) > 0 ){
			
			lightColor = LightColor.YELLOW;
		} else if((b & 0x10) > 0 ){
			
			lightColor = LightColor.WHITE;
		}
		
        twinkle = (b & 0x08) > 0 ;
		
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.ChnLightCode;
	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean supportChannel() {
		return true;
	}

	

	
	@Override
	public String toString() {
		return "ChannelLightData [twinkle=" + twinkle + ", lightColor=" + lightColor + ", chnIndex=" + chnIndex + "]";
	}

	public boolean isTwinkle() {
		return twinkle;
	}

	public void setTwinkle(boolean twinkle) {
		this.twinkle = twinkle;
	}

	public LightColor getLightColor() {
		return lightColor;
	}

	public void setLightColor(LightColor lightColor) {
		this.lightColor = lightColor;
	}
	
	
    
	
	
}
