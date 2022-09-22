package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 增强的风机监测协议，带地址
 * @author Administrator
 *
 */
public class AnotherFanStateQueryData extends Data implements Queryable, Responsable {
   
	public final static int   HEAT_FAN_BYTE_SIZE = 4;
	public final static int   TURBO_FAN_BYTE_SIZE = 1;
	private int         heatFanCount;
	private List<Byte>  heatFanStateFlag =  new ArrayList<Byte>(HEAT_FAN_BYTE_SIZE); //4个字节的风机状态标志
	private List<Byte>  heatFanSwitchFlag =  new ArrayList<Byte>(HEAT_FAN_BYTE_SIZE); //4个字节的风机运行标志
	private int         turboFanCount;
	private List<Byte>  turboFanStateFlag =  new ArrayList<Byte>(TURBO_FAN_BYTE_SIZE); //1个字节的风机状态标志
	private List<Byte>  turboFanSwitchFlag =  new ArrayList<Byte>(TURBO_FAN_BYTE_SIZE); //1个字节的风机运行标志
	
	@Override
	public boolean supportUnit() {
		
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) heatFanCount);
		//运行状态
		data.addAll(heatFanSwitchFlag.subList(0, HEAT_FAN_BYTE_SIZE));
		
		if(heatFanStateFlag.size() < HEAT_FAN_BYTE_SIZE) {
			
			throw new RuntimeException("error fan state flag size : " + heatFanStateFlag.size());
		}
		//风机状态
		data.addAll(heatFanStateFlag.subList(0, HEAT_FAN_BYTE_SIZE));
		//涡轮风机个数
		data.add((byte) turboFanCount);
		data.addAll(turboFanSwitchFlag.subList(0, TURBO_FAN_BYTE_SIZE));
		data.addAll(turboFanStateFlag.subList(0, TURBO_FAN_BYTE_SIZE));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		heatFanCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		heatFanSwitchFlag.clear();
		heatFanSwitchFlag.addAll(new ArrayList<Byte>(data.subList(index, index + HEAT_FAN_BYTE_SIZE)));
		index += HEAT_FAN_BYTE_SIZE;
		heatFanStateFlag.clear();
		heatFanStateFlag.addAll(new ArrayList<Byte>(data.subList(index, index + HEAT_FAN_BYTE_SIZE)));
		index += HEAT_FAN_BYTE_SIZE;
		//
		turboFanCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		turboFanSwitchFlag.clear();
		turboFanSwitchFlag.addAll(new ArrayList<Byte>(data.subList(index, index + TURBO_FAN_BYTE_SIZE)));
		index += TURBO_FAN_BYTE_SIZE;
		turboFanStateFlag.clear();
		turboFanStateFlag.addAll(new ArrayList<Byte>(data.subList(index, index + TURBO_FAN_BYTE_SIZE)));
		index += TURBO_FAN_BYTE_SIZE;

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.AnotherFanStateCode;
	}

	public int getHeatFanCount() {
		return heatFanCount;
	}

	public void setHeatFanCount(int heatFanCount) {
		this.heatFanCount = heatFanCount;
	}

	public List<Byte> getHeatFanStateFlag() {
		return heatFanStateFlag;
	}

	public void setHeatFanStateFlag(List<Byte> heatFanStateFlag) {
		this.heatFanStateFlag = heatFanStateFlag;
	}

	public List<Byte> getHeatFanSwitchFlag() {
		return heatFanSwitchFlag;
	}

	public void setHeatFanSwitchFlag(List<Byte> heatFanSwitchFlag) {
		this.heatFanSwitchFlag = heatFanSwitchFlag;
	}

	public int getTurboFanCount() {
		return turboFanCount;
	}

	public void setTurboFanCount(int turboFanCount) {
		this.turboFanCount = turboFanCount;
	}

	public List<Byte> getTurboFanStateFlag() {
		return turboFanStateFlag;
	}

	public void setTurboFanStateFlag(List<Byte> turboFanStateFlag) {
		this.turboFanStateFlag = turboFanStateFlag;
	}

	public List<Byte> getTurboFanSwitchFlag() {
		return turboFanSwitchFlag;
	}

	public void setTurboFanSwitchFlag(List<Byte> turboFanSwitchFlag) {
		this.turboFanSwitchFlag = turboFanSwitchFlag;
	}
	
	

}
