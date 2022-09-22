package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 极性灯通信协议
 * @author Administrator
 *
 */
public class PoleLightData extends Data implements Configable, Queryable, Responsable {
    
	/**
	 * 灯颜色定义
	 */
	public final static byte GREEN = 0x01;
	public final static byte YELLOW = 0x02;
	public final static byte RED = 0x04;
	
	private byte   colorFlag ;
	private short  lightFlag;
	
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
        data.add(colorFlag);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(lightFlag), 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//颜色标志位
		colorFlag = data.get(index++);
		//灯亮时间标志位
		lightFlag  = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.PoleLightCode;
	}

	public byte getColorFlag() {
		return colorFlag;
	}

	public void setColorFlag(byte colorFlag) {
		this.colorFlag = colorFlag;
	}

	public short getLightFlag() {
		return lightFlag;
	}

	public void setLightFlag(short lightFlag) {
		this.lightFlag = lightFlag;
	}
	
	

}
