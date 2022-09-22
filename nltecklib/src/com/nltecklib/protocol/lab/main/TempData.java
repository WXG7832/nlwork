package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

//不由主控控制
@Deprecated   
public class TempData extends Data implements Cloneable,Configable,Queryable,Responsable {
	
	private double tempConstant = 40; //恒温值
	public double upper = 50; //报警上限
	public double lower = 0; //报警下限
	
	private int  protectMinute; //恒温时间保护

	private boolean syncTempOpen; //温控系统开关
	private boolean tempControlOpen; //温控启动保护开关
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.add((byte)mainIndex);
		data.add((byte)chnIndex);
		
		data.addAll(Arrays.asList(ProtocolUtil.split((int)(tempConstant * 10), 2, true)));

		data.addAll(Arrays.asList(ProtocolUtil.split((int) upper * 10, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((int) lower * 10, 2, true)));
		
		data.add((byte) protectMinute);

		data.add((byte) (syncTempOpen ? 1 : 0));
		data.add((byte) (tempControlOpen ? 1 : 0));
		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		mainIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		tempConstant = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		upper = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		lower = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		protectMinute = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

		int flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		syncTempOpen = flag == 1;
		flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		tempControlOpen = flag == 1;
	
	}
	
	@Override
	public Code getCode() {
		
		return null;
	}
	
	
	public double getTempConstant() {
		return tempConstant;
	}

	public void setTempConstant(double tempConstant) {
		this.tempConstant = tempConstant;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public int getProtectMinute() {
		return protectMinute;
	}

	public void setProtectMinute(int protectMinute) {
		this.protectMinute = protectMinute;
	}

	public boolean isSyncTempOpen() {
		return syncTempOpen;
	}

	public void setSyncTempOpen(boolean syncTempOpen) {
		this.syncTempOpen = syncTempOpen;
	}

	public boolean isTempControlOpen() {
		return tempControlOpen;
	}

	public void setTempControlOpen(boolean tempControlOpen) {
		this.tempControlOpen = tempControlOpen;
	}
	

	@Override
	public String toString() {
		return "TempData [tempConstant=" + tempConstant + ", upper=" + upper + ", lower=" + lower + ", protectMinute="
				+ protectMinute + ", syncTempOpen=" + syncTempOpen + ", tempControlOpen=" + tempControlOpen + "]";
	}

	@Override
	public boolean supportChannel() {
		return false;
	}
	
	
}
