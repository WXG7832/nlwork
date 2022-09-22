package com.nltecklib.protocol.li.check;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description  26：驱动板状态查询： 0x18 不支持配置，支持查询（HK用协议）
 * @author yaohui 
 * @version
 * @date 2020年5月14日上午11:27:59
 */
public class CheckHKDriverStateData extends Data implements Queryable, Responsable {

	 private int temp;
	 private boolean stateOfPTwelve_OK;
	 private boolean stateOfNTwelve_OK;
	 private boolean stateOfPFive_OK;
	 private boolean stateOfPSixPointFive_OK;
	 
	
	
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public boolean isStateOfPTwelve_OK() {
		return stateOfPTwelve_OK;
	}
	public void setStateOfPTwelve_OK(boolean stateOfPTwelve_OK) {
		this.stateOfPTwelve_OK = stateOfPTwelve_OK;
	}
	public boolean isStateOfNTwelve_OK() {
		return stateOfNTwelve_OK;
	}
	public void setStateOfNTwelve_OK(boolean stateOfNTwelve_OK) {
		this.stateOfNTwelve_OK = stateOfNTwelve_OK;
	}
	public boolean isStateOfPFive_OK() {
		return stateOfPFive_OK;
	}
	public void setStateOfPFive_OK(boolean stateOfPFive_OK) {
		this.stateOfPFive_OK = stateOfPFive_OK;
	}
	public boolean isStateOfPSixPointFive_OK() {
		return stateOfPSixPointFive_OK;
	}
	public void setStateOfPSixPointFive_OK(boolean stateOfPSixPointFive_OK) {
		this.stateOfPSixPointFive_OK = stateOfPSixPointFive_OK;
	}
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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
		// TODO Auto-generated method stub		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) temp);
		data.add((byte) (stateOfPTwelve_OK ? 0x00 : 0x01));
		data.add((byte) (stateOfNTwelve_OK ? 0x00 : 0x01));
		data.add((byte) (stateOfPFive_OK ? 0x00 : 0x01));
		data.add((byte) (stateOfPSixPointFive_OK ? 0x00 : 0x01));
	
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		int index = 0;
		data= encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		temp = ProtocolUtil.getUnsignedByte(data.get(index++));
		stateOfPTwelve_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		stateOfNTwelve_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		stateOfPFive_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		stateOfPSixPointFive_OK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			
	}
	
	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.HKDriverState;
	}
	
	
	
	
}
