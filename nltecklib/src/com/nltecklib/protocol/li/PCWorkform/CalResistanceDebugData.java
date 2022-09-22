package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月8日 下午1:27:12
* 在校准主控通道映射情况下，driverIndex在这里并不代表校准板号，而是代表通道号；
* 因chnIndex在这个协议里有其他含义，代表档位量程
* 在校准主控没有开通通道映射情况下，driverIndex仍然代表校准板号
*/
public class CalResistanceDebugData extends Data implements Configable, Queryable, Responsable {
   
	private double resistance;//电阻系数
	
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resistance * Math.pow(10, 6)), 4, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if (unitIndex > WorkPattern.values().length - 1) {

			throw new RuntimeException("error WorkPattern mode index : " + unitIndex);
		}
		
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		resistance = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 6);
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalResistanceDebugCode;
	}

	

	@Override
	public String toString() {
		return "CalResistanceDebugData [resistance=" + resistance + "]";
	}

	public WorkPattern getWorkPattern() {
		return  WorkPattern.values()[unitIndex];
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.unitIndex = workPattern.ordinal();
	}

	public int getRange() {
		return chnIndex;
	}

	public void setRange(int range) {
		this.chnIndex = range;
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}
	
	

}
