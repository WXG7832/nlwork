package com.nltecklib.protocol.lab.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 欧姆容温控表温度设置
 * @author Administrator
 *
 */
public class OMRTempData extends Data implements Configable,Queryable,Responsable {

	//温控表地址
	//private int omrIndex;  //使用板号
	//温度
	private double temp;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)temp, 2, true)));	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;

		temp = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;	
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.OMRMeterTempCode;
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

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}
	@Override
	public String toString() {
		return "OMRTempData [temp=" + temp + "]";
	}
	
	
	
}
