package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  xiao_wang
* @version 创建时间：2022年8月29
* 注意此协议查询需要QueryDecorator类里带params去查询
*/
public class ResistanceModeRelayDebugData extends Data implements Configable, Queryable, Responsable {
    
	private WorkPattern workPattern;//工作模式
	private byte   relayIndex; //继电器编号
	private int range;//档位量程
	private double resistance;//电阻系数
	
	
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
		return false;
	}
	
	
	
	

	public WorkPattern getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.workPattern = workPattern;
	}

	public byte getRelayIndex() {
		return relayIndex;
	}

	public void setRelayIndex(byte relayIndex) {
		this.relayIndex = relayIndex;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add(relayIndex);
		data.add((byte) workPattern.ordinal());
		data.add((byte) range);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resistance * Math.pow(10, 6)), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		relayIndex = data.get(index++);
		
		int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (mode > WorkPattern.values().length - 1) {

			throw new RuntimeException("error WorkPattern mode index : " + mode);
		}
		workPattern = WorkPattern.values()[mode];
		
		range = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		resistance = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 6);
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalRelayResistanceDebugCode;
	}

	@Override
	public String toString() {
		return "ResistanceModeRelayData [workPattern=" + workPattern + ", relayIndex=" + relayIndex + ", range=" + range
				+ ", resistance=" + resistance + "]";
	}
	
	

}
