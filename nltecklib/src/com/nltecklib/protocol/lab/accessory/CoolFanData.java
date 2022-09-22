package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.HeatFanType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 散热风机控制
 * @author Administrator
 *
 */
public class CoolFanData extends Data implements Configable,Queryable,Responsable {

	//private int group = 0xff;  //组号,0xff表示所有组风机   风机组号使用
	
	/*
	 * 0:进风风扇, 1:出风风扇
	 * 现在不区分进出风扇 发0x00即可
	 */
	//private int fanType = 0x00;  
	private boolean working;  //开关
	
	public CoolFanData() {
		setGroup(0xFF);
		setFanType(HeatFanType.IN);
	}
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void encode() {
		
		data.add((byte) (working ? 0x01 : 0x00));	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		
		working = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;	
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.CoolFanCode;
	}
	
	@Override
	public boolean supportChannel() {
		return true;
	}

	public int getGroup() {
		return mainIndex;
	}

	public void setGroup(int group) {
		this.mainIndex = group;
	}

	public HeatFanType getFanType() {
		return HeatFanType.values()[chnIndex];
	}

	public void setFanType(HeatFanType fanType) {
		this.chnIndex = fanType.ordinal();
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	@Override
	public String toString() {
		return "HeatFanData [working=" + working + "]";
	}
	
	
}
