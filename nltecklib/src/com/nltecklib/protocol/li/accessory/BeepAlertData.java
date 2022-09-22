package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月23日 下午4:29:05
* 蜂鸣报警协议
*/
public class BeepAlertData extends Data implements Configable, Queryable, Responsable {
    
	private int  seconds; //蜂鸣时间
	private short  flag;  //蜂鸣方式标志
	
	
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
		
		data.add((byte) driverIndex);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(seconds), 2,true)));
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(flag), 2,true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 蜂鸣时间
		seconds = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 蜂鸣方式
		flag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.BeepCode;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public short getFlag() {
		return flag;
	}

	public void setFlag(short flag) {
		this.flag = flag;
	}
	
	
	

}
