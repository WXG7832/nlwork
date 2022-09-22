package com.nltecklib.protocol.lab.pickup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Data.Generation;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PProtectionData extends Data implements Configable, Queryable, Responsable {
    
	private double  voltUpper;
	private double  currUpper;

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		/**
		 * 电压电流字节数
		 */
		int vaLen = Data.getGeneration() == Generation.TH1 ? 3 : 4;
		//过压上限值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltUpper * 1000), vaLen, true)));
		//过流上限值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(currUpper * 1000), vaLen , true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		/**
		 * 电压电流字节数
		 */
		int vaLen = Data.getGeneration() == Generation.TH1 ? 3 : 4;
		
		//过压值
		voltUpper = (double)ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true) / 1000;
		index += vaLen;
		//过流值
		currUpper = (double)ProtocolUtil.compose(data.subList(index, index + vaLen).toArray(new Byte[0]), true) / 1000;
		index += vaLen;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.ProtectCode;
	}

	public double getVoltUpper() {
		return voltUpper;
	}

	public void setVoltUpper(double voltUpper) {
		this.voltUpper = voltUpper;
	}

	public double getCurrUpper() {
		return currUpper;
	}

	public void setCurrUpper(double currUpper) {
		this.currUpper = currUpper;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "ProtectionData [voltUpper=" + voltUpper + ", currUpper=" + currUpper + "]";
	}
	
	

}
