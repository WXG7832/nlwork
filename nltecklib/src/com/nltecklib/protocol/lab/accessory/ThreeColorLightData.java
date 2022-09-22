package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 三色灯及报警
 * @author Administrator
 */
public class ThreeColorLightData extends Data implements Configable,Queryable,Responsable {

	public enum LightColor {
		
		GREEN(0x01) , YELLOW(0x02) , RED(0x04);
		
		private int code;
		private LightColor(int code) {
			
			this.code = code;
		}
		
		public int getCode() {
			
			return code;
		}
		
		public static LightColor valueOf(int code) {
			
			  switch(code) {
			  case 0x01:
				  return GREEN;
			  case 0x02:
				  return YELLOW;
			  case 0x04:
				  return RED;
			  
			  }
			  
			  return null;
		}
		
	}
	
	
	
	
	private LightColor colorFlag ;  //颜色
	private short lightFlag;  //闪烁标志
	
	@Override
	public void encode() {
		data.add((byte)colorFlag.getCode());
		data.add((byte) lightFlag);		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		//颜色
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if((colorFlag = LightColor.valueOf(code)) == null) {
			
			throw new RuntimeException("error light color code:" + code);
		}
		
		//闪烁规则
		lightFlag = data.get(index++);
	}
	
	@Override
	public Code getCode() {
		
		return AccessoryCode.ThreeColorLightCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}
	
	@Override
	public boolean supportMain() {
		return false;
	}

	

	public LightColor getColorFlag() {
		return colorFlag;
	}

	public void setColorFlag(LightColor colorFlag) {
		this.colorFlag = colorFlag;
	}

	public short getLightFlag() {
		return lightFlag;
	}

	public void setLightFlag(short lightFlag) {
		this.lightFlag = lightFlag;
	}

	@Override
	public String toString() {
		return "ThreeColorLightData [colorFlag=" + colorFlag + ", lightFlag=" + lightFlag + "]";
	}
	
	
	
}
