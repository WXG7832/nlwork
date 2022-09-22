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

/**
 * 回检板电压保护
 * @author Administrator
 *
 */
public class CheckVoltProtectData extends Data implements Configable, Queryable, Responsable {
    
	private double voltOffset; //压差最大值
	private double resisterOffset; //阻止最大值
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		data.add((byte) unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltOffset * 10), 2,true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(resisterOffset * 10), 2,true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex  =  ProtocolUtil.getUnsignedByte(data.get(index++));
		voltOffset = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
		resisterOffset = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CheckProtectCode;
	}

	public double getVoltOffset() {
		return voltOffset;
	}

	public void setVoltOffset(double voltOffset) {
		this.voltOffset = voltOffset;
	}

	public double getResisterOffset() {
		return resisterOffset;
	}

	public void setResisterOffset(double resisterOffset) {
		this.resisterOffset = resisterOffset;
	}

	@Override
	public String toString() {
		return "CheckVoltProtectData [voltOffset=" + voltOffset + ", resisterOffset=" + resisterOffset + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof CheckVoltProtectData){
			
			CheckVoltProtectData cvpd = (CheckVoltProtectData) obj;
			if (cvpd.getUnitIndex() == this.unitIndex 
					&& cvpd.getResisterOffset() == this.resisterOffset
					&& cvpd.getVoltOffset() == this.voltOffset) {
				
				return true;
			}
		}
		return false;
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
