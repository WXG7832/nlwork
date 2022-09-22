package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年5月17日 上午11:15:05
* 全局保护
*/
public class EnvProtectData extends Data implements Configable, Queryable, Responsable {
   
	private boolean enable;
	private double  voltageUpper;
	private double  currentUpper;
	private double  chnTempUpper; //通道超温保护
	private double  chnTempLower; //通道低温保护
	
	@Override
	public boolean supportMain() {
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
		
		data.add((byte) (enable ? 0x01 : 0x00));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltageUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currentUpper * 10), 3,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnTempUpper * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnTempLower * 10), 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		enable = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		voltageUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currentUpper = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 10;
		index += 3;
		chnTempUpper = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnTempLower = (double)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.EnvProtectCode;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public double getVoltageUpper() {
		return voltageUpper;
	}

	public void setVoltageUpper(double voltageUpper) {
		this.voltageUpper = voltageUpper;
	}

	public double getCurrentUpper() {
		return currentUpper;
	}

	public void setCurrentUpper(double currentUpper) {
		this.currentUpper = currentUpper;
	}

	public double getChnTempUpper() {
		return chnTempUpper;
	}

	public void setChnTempUpper(double chnTempUpper) {
		this.chnTempUpper = chnTempUpper;
	}

	public double getChnTempLower() {
		return chnTempLower;
	}

	public void setChnTempLower(double chnTempLower) {
		this.chnTempLower = chnTempLower;
	}

	@Override
	public String toString() {
		return "EnvProtectData [enable=" + enable + ", voltageUpper=" + voltageUpper + ", currentUpper=" + currentUpper
				+ ", chnTempUpper=" + chnTempUpper + ", chnTempLower=" + chnTempLower + "]";
	}

	
	

}
