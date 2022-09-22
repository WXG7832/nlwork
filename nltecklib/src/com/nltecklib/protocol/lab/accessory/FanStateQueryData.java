package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ·ç»ú¼à²â×´Ì¬
 * @author Administrator
 *
 */
public class FanStateQueryData extends Data implements Queryable,Responsable  {

	private final static int   HEAT_FAN_BYTE_SIZE = 4;
	private final static int   TURBO_FAN_BYTE_SIZE = 1;
	
	//É¢ÈÈ·ç»ú
	private int heatFanCount;
	private List<Byte>  heatFanSwitchFlag =  new ArrayList<Byte>(HEAT_FAN_BYTE_SIZE);  //ÔËÐÐ×´Ì¬
	private List<Byte>  heatFanStateFlag =  new ArrayList<Byte>(HEAT_FAN_BYTE_SIZE);  //¹ÊÕÏ×´Ì¬
	
	//ÎÐÂÖ·ç»ú
	private int turboFanCount;
	private List<Byte>  turboFanSwitchFlag =  new ArrayList<Byte>(TURBO_FAN_BYTE_SIZE);  //ÔËÐÐ×´Ì¬
	private List<Byte>  turboFanStateFlag =  new ArrayList<Byte>(TURBO_FAN_BYTE_SIZE);  //¹ÊÕÏ×´Ì¬
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void encode() {
		
		data.add((byte) heatFanCount);
		data.addAll(heatFanSwitchFlag.subList(0, HEAT_FAN_BYTE_SIZE));
		data.addAll(heatFanStateFlag.subList(0, HEAT_FAN_BYTE_SIZE));
		
		data.add((byte) turboFanCount);
		data.addAll(turboFanSwitchFlag.subList(0, TURBO_FAN_BYTE_SIZE));
		data.addAll(turboFanStateFlag.subList(0, TURBO_FAN_BYTE_SIZE));	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		heatFanCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		heatFanSwitchFlag.clear();
		heatFanSwitchFlag.addAll(new ArrayList<Byte>(data.subList(index, index + HEAT_FAN_BYTE_SIZE)));
		index += HEAT_FAN_BYTE_SIZE;
		heatFanStateFlag.clear();
		heatFanStateFlag.addAll(new ArrayList<Byte>(data.subList(index, index + HEAT_FAN_BYTE_SIZE)));
		index += HEAT_FAN_BYTE_SIZE;
		
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
		
		return AccessoryCode.FanStateCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getHeatFanCount() {
		return heatFanCount;
	}

	public void setHeatFanCount(int heatFanCount) {
		this.heatFanCount = heatFanCount;
	}

	public List<Byte> getHeatFanSwitchFlag() {
		return heatFanSwitchFlag;
	}

	public void setHeatFanSwitchFlag(List<Byte> heatFanSwitchFlag) {
		this.heatFanSwitchFlag = heatFanSwitchFlag;
	}

	public List<Byte> getHeatFanStateFlag() {
		return heatFanStateFlag;
	}

	public void setHeatFanStateFlag(List<Byte> heatFanStateFlag) {
		this.heatFanStateFlag = heatFanStateFlag;
	}

	public int getTurboFanCount() {
		return turboFanCount;
	}

	public void setTurboFanCount(int turboFanCount) {
		this.turboFanCount = turboFanCount;
	}

	public List<Byte> getTurboFanSwitchFlag() {
		return turboFanSwitchFlag;
	}

	public void setTurboFanSwitchFlag(List<Byte> turboFanSwitchFlag) {
		this.turboFanSwitchFlag = turboFanSwitchFlag;
	}

	public List<Byte> getTurboFanStateFlag() {
		return turboFanStateFlag;
	}

	public void setTurboFanStateFlag(List<Byte> turboFanStateFlag) {
		this.turboFanStateFlag = turboFanStateFlag;
	}
	@Override
	public String toString() {
		return "FanStateQueryData [heatFanCount=" + heatFanCount + ", heatFanSwitchFlag=" + heatFanSwitchFlag
				+ ", heatFanStateFlag=" + heatFanStateFlag + ", turboFanCount=" + turboFanCount
				+ ", turboFanSwitchFlag=" + turboFanSwitchFlag + ", turboFanStateFlag=" + turboFanStateFlag + "]";
	}
	
	
	
}
