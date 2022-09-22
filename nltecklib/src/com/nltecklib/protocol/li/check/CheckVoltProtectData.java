package com.nltecklib.protocol.li.check;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class CheckVoltProtectData extends Data implements Configable, Queryable, Responsable {
    
	private double overThreashold;
	private double backupThreashold;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		//过压保护值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(overThreashold * 10), 2, true)));
		//备份
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(backupThreashold * 10), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
	    unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    driverIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    overThreashold =  (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	    backupThreashold =  (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
	    index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.DeviceProtectCode;
	}

	public double getOverThreashold() {
		return overThreashold;
	}

	public void setOverThreashold(double overThreashold) {
		this.overThreashold = overThreashold;
	}

	public double getBackupThreashold() {
		return backupThreashold;
	}

	public void setBackupThreashold(double backupThreashold) {
		this.backupThreashold = backupThreashold;
	}
	
	

}
