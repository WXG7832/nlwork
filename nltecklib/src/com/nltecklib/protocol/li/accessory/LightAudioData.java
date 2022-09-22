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
 * 声光控制协议
 * @author Administrator
 *
 */
public class LightAudioData extends Data implements Configable, Queryable, Responsable {
    
	
	private byte   colorFlag ;
	private short  lightFlag;
	private short  audioFlag;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

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
		// TODO Auto-generated method stub
		data.add((byte) driverIndex);
        data.add(colorFlag);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(lightFlag), 2,true)));
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(audioFlag), 2,true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//颜色标志位
		colorFlag = data.get(index++);
		//灯亮时间标志位
		lightFlag  = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
        //蜂鸣器标志位
		audioFlag  = (short) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.LightAudioCode;
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

	public short getAudioFlag() {
		return audioFlag;
	}

	public void setAudioFlag(short audioFlag) {
		this.audioFlag = audioFlag;
	}
	
	

}
