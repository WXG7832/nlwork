package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.ChnOptType;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ChannelOperateData extends Data implements Configable,Responsable {
	
	
	
	private ChnOptType optType = ChnOptType.STOP;
	private int channelCount;
	private byte   flag; //操作的通道标记，bit 1 表示选中该通道 ， 0 表示不选中 

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public ChannelOperateData() {
		
		
	}
	
	@Override
	public void encode() {
	
		//操作类型
		data.add((byte)optType.ordinal());
		//通道操作个数
		data.add((byte)channelCount);
		//通道操作标记
		data.add((byte) (flag & 0xff));
			
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		
		// 操作类型
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ChnOptType.values().length - 1) {

			throw new RuntimeException("error chn opt type code :" + code);
		}
		optType = ChnOptType.values()[code];
		
		//通道操作个数
		channelCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		//操作标记
		flag = data.get(index++);
		
	}
	
	public void addChannel(int chnIndex) {

		flag = (byte) (flag | (0x01 << chnIndex));
		
	}
	
	public void clearFlag() {

		flag = 0;
	}
	
	@Override
	public Code getCode() {
		return MainCode.ChnOperateCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public ChnOptType getOptType() {
		return optType;
	}

	public void setOptType(ChnOptType optType) {
		this.optType = optType;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "ChannelOperateData [optType=" + optType + ", channelCount=" + channelCount + ", flag=" + flag + "]";
	}

	
	
	
}
