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
 * 计量通道  0x09
 * @author admin
 */
public class DriverMeterData extends Data  implements Configable, Queryable, Responsable{
    
	private boolean POLE_POS;

	private WorkMode workMode;
	
	private double calculateDot;//实际计量万用表值

	private double programK;
	
	private double programB;
	
	private long   program;
	
//	public static final int CAL_BIT_COUNT = 2;
//	public static final int K_BIT_COUNT = 7;
//	public static final int B_BIT_COUNT = 7;
	
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
		
   	    data.add((byte) (POLE_POS ? 0x01 : 0x00));
   	    
   	    data.add((byte) workMode.ordinal());
   	    
   	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * Math.pow(10, currentResolution)), 3, true)));
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
		

		calculateDot = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true)
				/ Math.pow(10, currentResolution);
		index += 3;
		programK = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, programKResolution);
		index += 4;
		programB = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, programBResolution);
		index += 4;
		
		program = ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);

		
	}

	
	public boolean isPOLE_POS() {
		return POLE_POS;
	}

	public void setPOS_POLE(boolean POLE_POS) {
		this.POLE_POS = POLE_POS;
	}

	
	@Override
	public Code getCode() {
		return DriverCode.MeterCode;
	}



	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	
	public double getProgramK() {
		return programK;
	}

	public void setProgramK(double programK) {
		this.programK = programK;
	}

	public double getProgramB() {
		return programB;
	}

	public void setProgramB(double programB) {
		this.programB = programB;
	}

	public long getProgram() {
		return program;
	}

	public void setProgram(long program) {
		this.program = program;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	
}
