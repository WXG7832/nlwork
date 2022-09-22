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
 * 欧姆容温控表开关
 * @author Administrator
 *
 */
public class OMRTempSwitchData extends Data implements Configable,Queryable,Responsable {

	//温控表地址
	//private int omrIndex;  //使用板号
	//开关
	private boolean working;
	
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
		return AccessoryCode.OMRMeterSwitchCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getOmrIndex() {
		return mainIndex;
	}

	public void setOmrIndex(int omrIndex) {
		this.mainIndex = omrIndex;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}
	@Override
	public String toString() {
		return "OMRTempSwitchData [working=" + working + "]";
	}
	
	
}
