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
 * 蜂鸣器
 * @author Administrator
 *
 */
public class BuzzerData extends Data implements Configable,Queryable,Responsable {

	private short audioFlag;  //报警标志,蜂鸣器标志
	private int   circleCount; //执行周期数;0表示循环周期执行
	
	@Override
	public void encode() {
		
		data.add((byte) circleCount);
		data.add((byte) audioFlag);				
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		
		//周期
		circleCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		//报警
		audioFlag = data.get(index++);
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.BuzzerCode;
	}
	@Override
	public boolean supportChannel() {
		return false;
	}
	@Override
	public boolean supportMain() {
		return false;
	}

	public short getAudioFlag() {
		return audioFlag;
	}

	public void setAudioFlag(short audioFlag) {
		this.audioFlag = audioFlag;
	}

	public int getCircleCount() {
		return circleCount;
	}

	public void setCircleCount(int circleCount) {
		this.circleCount = circleCount;
	}

	@Override
	public String toString() {
		return "BuzzerData [audioFlag=" + audioFlag + ", circleCount=" + circleCount + "]";
	}

	
	
	
	
}
