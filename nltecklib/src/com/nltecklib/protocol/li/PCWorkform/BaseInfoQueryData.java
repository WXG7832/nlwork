package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BaseInfoQueryData extends Data implements Queryable,Responsable{

	private String deviceMac="";
	private int logicCount;
	private byte logicState;
	private int checkCount;
	private byte checkState;
	private int deviceDriverCount;
	private int deviceDriverChnCount;
	private String calBoxMac="";
	private int calCount;
	private byte calState;
	private int calChnCount;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		data.addAll(ProtocolUtil.encodeMac(deviceMac));
		data.add((byte)logicCount);
		data.add(logicState);
		data.add((byte)checkCount);
		data.add(checkState);
		data.add((byte)deviceDriverCount);
		data.add((byte)deviceDriverChnCount);
		data.addAll(ProtocolUtil.encodeMac(calBoxMac));
		data.add((byte)calCount);
		data.add(calState);
		data.add((byte)calChnCount);
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data=encodeData;
		int index=0;
		deviceMac=ProtocolUtil.decodeMac(data.subList(index,index+6));
		index+=6;
		logicCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		logicState=(byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		checkCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		checkState=(byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		deviceDriverCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		deviceDriverChnCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		calBoxMac=ProtocolUtil.decodeMac(data.subList(index,index+6));
		index+=6;
		calCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		calState=(byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		calChnCount=ProtocolUtil.getUnsignedByte(data.get(index++));
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.BaseInfoQueryCode;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public int getLogicCount() {
		return logicCount;
	}

	public void setLogicCount(int logicCount) {
		this.logicCount = logicCount;
	}

	public byte getLogicState() {
		return logicState;
	}

	public void setLogicState(byte logicState) {
		this.logicState = logicState;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public byte getCheckState() {
		return checkState;
	}

	public void setCheckState(byte checkState) {
		this.checkState = checkState;
	}



	public int getDeviceDriverCount() {
		return deviceDriverCount;
	}

	public void setDeviceDriverCount(int deviceDriverCount) {
		this.deviceDriverCount = deviceDriverCount;
	}

	public int getDeviceDriverChnCount() {
		return deviceDriverChnCount;
	}

	public void setDeviceDriverChnCount(int deviceDriverChnCount) {
		this.deviceDriverChnCount = deviceDriverChnCount;
	}

	public String getCalBoxMac() {
		return calBoxMac;
	}

	public void setCalBoxMac(String calBoxMac) {
		this.calBoxMac = calBoxMac;
	}

	public int getCalCount() {
		return calCount;
	}

	public void setCalCount(int calCount) {
		this.calCount = calCount;
	}

	public byte getCalState() {
		return calState;
	}

	public void setCalState(byte calState) {
		this.calState = calState;
	}

	public int getCalChnCount() {
		return calChnCount;
	}

	public void setCalChnCount(int calChnCount) {
		this.calChnCount = calChnCount;
	}

	@Override
	public String toString() {
		return "BaseInfoQueryData [deviceMac=" + deviceMac + ", logicCount=" + logicCount + ", logicState=" + logicState
				+ ", checkCount=" + checkCount + ", checkState=" + checkState + ", deviceDriverCount="
				+ deviceDriverCount + ", deviceDriverChnCount=" + deviceDriverChnCount + ", calBoxMac=" + calBoxMac
				+ ", calCount=" + calCount + ", calState=" + calState + ", calChnCount=" + calChnCount + "]";
	}
	
	

}
