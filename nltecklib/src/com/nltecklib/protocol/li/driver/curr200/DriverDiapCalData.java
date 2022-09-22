package com.nltecklib.protocol.li.driver.curr200;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 膜片校准
 * @Desc：   
 * @author：LLC   
 * @Date：2021年10月15日 下午6:13:05   
 * @version
 */
public class DriverDiapCalData  extends Data implements  Configable, Queryable, Responsable {

	//膜片编号
	private int diapIndex;
	//是否正极性
	private boolean positivePole;
	//工作方式
	private WorkMode workMode;
	//精度3个档
	private short  highPrecision;
	//程控电压
	private long  specialVoltage;
	//程控电流
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
		
		data.add((byte) diapIndex);
		
		data.add((byte) (positivePole ? 0x01 : 0x00));
		
		
   	    data.add((byte) workMode.ordinal());
   	    
   	    data.add((byte) highPrecision);
   	    
   	    data.addAll(Arrays.asList(ProtocolUtil.split(specialVoltage, 2, true)));
   	    
   	    data.addAll(Arrays.asList(ProtocolUtil.split(specialCurrent, 3, true)));
   	    
   	    
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		

		int index = 0;
		data = encodeData;
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		diapIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		positivePole = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(code > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error work mode code : " + code);
		}
		workMode = WorkMode.values()[code];
		
		highPrecision = (short) ProtocolUtil.getUnsignedByte(data.get(index++));
		
		specialVoltage = ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//电流
		specialCurrent = ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true);
		index += 3;
		
	}

	@Override
	public Code getCode() {
		return DriverCode.Driver200aDiapCalCode;
	}

	public int getDiapIndex() {
		return diapIndex;
	}

	public void setDiapIndex(int diapIndex) {
		this.diapIndex = diapIndex;
	}

	public boolean isPositivePole() {
		return positivePole;
	}

	public void setPositivePole(boolean positivePole) {
		this.positivePole = positivePole;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}


	public long getSpecialVoltage() {
		return specialVoltage;
	}

	public void setSpecialVoltage(long specialVoltage) {
		this.specialVoltage = specialVoltage;
	}

	public long getSpecialCurrent() {
		return specialCurrent;
	}

	public void setSpecialCurrent(long specialCurrent) {
		this.specialCurrent = specialCurrent;
	}

	public short getHighPrecision() {
		return highPrecision;
	}

	public void setHighPrecision(short highPrecision) {
		this.highPrecision = highPrecision;
	}

	
}
