package com.nltecklib.protocol.lab.pickup;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ADCheck;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalibrateCheck;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年1月3日 下午5:06:20
* 类说明
*/
public class PHardErrInfoData extends Data implements Queryable, Responsable {
   
	
	private ADCheck  adCheck = ADCheck.OK;
	private CalibrateCheck  calCheck = CalibrateCheck.OK;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) adCheck.ordinal());
		data.add((byte) calCheck.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ADCheck.values().length - 1) {
			
			throw new RuntimeException("error ad check code:" + code);
		}
		adCheck = ADCheck.values()[code];
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > CalibrateCheck.values().length - 1) {
			
			throw new RuntimeException("error ad cal code:" + code);
		}
		calCheck = CalibrateCheck.values()[code];
		

	}

	@Override
	public Code getCode() {
		
		return ChipPickupCode.HardErrInfoCode;
	}

	public ADCheck getAdCheck() {
		return adCheck;
	}

	public void setAdCheck(ADCheck adCheck) {
		this.adCheck = adCheck;
	}

	public CalibrateCheck getCalCheck() {
		return calCheck;
	}

	public void setCalCheck(CalibrateCheck calCheck) {
		this.calCheck = calCheck;
	}

	@Override
	public String toString() {
		return "PHardErrInfoData [adCheck=" + adCheck + ", calCheck=" + calCheck + "]";
	}
	
	

}
