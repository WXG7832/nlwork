package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class OverChargeProtectData extends Data implements Configable, Queryable, Responsable {
   
	private double overChargeVolt;
	private double overDischargeVolt;
	
	@Override
	public void encode() {
		
        
		 data.add((byte)unitIndex);
		 data.addAll(Arrays.asList(ProtocolUtil.split((long)(overChargeVolt * 10), 2, true)));
		 data.addAll(Arrays.asList(ProtocolUtil.split((long)(overDischargeVolt * 10), 2, true)));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
	    data = encodeData;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    overChargeVolt  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	    overDischargeVolt  = (double)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	}

	@Override
	public Code getCode() {
		
		return MainCode.DeviceProtectCode;
	}

	@Override
	public boolean supportUnit() {
		
		return true;
	}

	public double getOverChargeVolt() {
		return overChargeVolt;
	}

	public void setOverChargeVolt(double overChargeVolt) {
		this.overChargeVolt = overChargeVolt;
	}

	public double getOverDischargeVolt() {
		return overDischargeVolt;
	}

	public void setOverDischargeVolt(double overDischargeVolt) {
		this.overDischargeVolt = overDischargeVolt;
	}

	@Override
	public String toString() {
		return "OverChargeProtectData [overChargeVolt=" + overChargeVolt + ", overDischargeVolt=" + overDischargeVolt
				+ "]";
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

    
}
