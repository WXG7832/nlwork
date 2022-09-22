package com.nltecklib.protocol.li.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准通道
 * @author admin
 */
public class DriverCalculateData extends Data implements Configable, Queryable, Responsable{
   
	private boolean  POLE_POS;//极性
	
	private WorkMode workMode;
	
	private boolean  PRECISION_HIGH;//是否高精度
	
	private long  specialVoltage;
	
	private long  specialCurrent;
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		
   	    data.add((byte) (POLE_POS ? 0x01:0x00));
   	    
   	    data.add((byte) workMode.ordinal());
   	    
   	    data.add((byte) (PRECISION_HIGH ? 0x01:0x00));
   	    
   	    data.addAll(Arrays.asList(ProtocolUtil.split(specialVoltage, 2, true)));
   	    
   	    data.addAll(Arrays.asList(ProtocolUtil.split(specialCurrent, 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		POLE_POS = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(code > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error work mode code : " + code);
		}
		workMode = WorkMode.values()[code];
		
		PRECISION_HIGH = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
		specialVoltage = ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//电流
		specialCurrent = ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true);
		index += 3;
		
	}

	@Override
	public Code getCode() {
		return DriverCode.CalculateCode;
	}


	public boolean isPOLE_POS() {
		return POLE_POS;
	}

	public void setPOLE_POS(boolean POLE_POS) {
		this.POLE_POS = POLE_POS;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public boolean isPRECISION_HIGH() {
		return PRECISION_HIGH;
	}

	public void setPRECISION_HIGH(boolean pRECISION_HIGH) {
		PRECISION_HIGH = pRECISION_HIGH;
	}

	public double getSpecialVoltage() {
		return specialVoltage;
	}

	public void setSpecialVoltage(int specialVoltage) {
		this.specialVoltage = specialVoltage;
	}

	public double getSpecialCurrent() {
		return specialCurrent;
	}

	public void setSpecialCurrent(int specialCurrent) {
		this.specialCurrent = specialCurrent;
	}

	
}
