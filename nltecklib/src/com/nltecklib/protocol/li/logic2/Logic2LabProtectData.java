package com.nltecklib.protocol.li.logic2;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Logic2LabProtectData extends Data implements Configable, Responsable {
    
	private double voltageUpper;
	private double currentUpper;
	private short  chnFlag;
	
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
		
         data.add((byte) unitIndex);
         data.add((byte) driverIndex);
         data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnFlag), 2,true)));
         data.addAll(Arrays.asList(ProtocolUtil.split((long)(voltageUpper), 2,true)));
         data.addAll(Arrays.asList(ProtocolUtil.split((long)(currentUpper), 3,true)));
         
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnFlag = (short)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		voltageUpper = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		currentUpper = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;

	}
	
	

	@Override
	public String toString() {
		return "Logic2LabProtectData [voltageUpper=" + voltageUpper + ", currentUpper=" + currentUpper + ", chnFlag="
				+ chnFlag + "]";
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

	public short getChnFlag() {
		return chnFlag;
	}

	public void setChnFlag(short chnFlag) {
		this.chnFlag = chnFlag;
	}

	@Override
	public Code getCode() {
		
		return Logic2Code.LabProtectCode;
	}

}
