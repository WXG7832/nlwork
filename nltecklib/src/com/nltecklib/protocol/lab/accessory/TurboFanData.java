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
 * 涡轮风机控制
 * @author Administrator
 *
 */
public class TurboFanData extends Data implements Configable,Queryable,Responsable {

	//private int group = 0xff;  //组号,0xff表示所有组风机,使用板号
	private boolean working;  //开关
	
	public TurboFanData() {
		setGroup(0xFF);
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
		return AccessoryCode.TurboFanCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getGroup() {
		return mainIndex;
	}

	public void setGroup(int group) {
		this.mainIndex = group;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	@Override
	public String toString() {
		return "TurboFanData [working=" + working + "]";
	}
	
	
	
	
}
