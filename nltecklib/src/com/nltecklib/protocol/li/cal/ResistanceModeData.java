/**
 * 
 */
package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.OverTempAlert;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 电阻系数     功能码0x10  可读，可配 
* @version: v1.0.0
* @date: 2022年1月19日 下午2:16:41 
*
*/
public class ResistanceModeData extends Data implements Configable, Queryable, Responsable {

	
	private WorkPattern workPattern;//工作模式
	private int range;//档位量程
	private byte resistanceIndex; // 第几颗电阻
	private double resistance;//电阻系数
	
	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);		
		data.add((byte) workPattern.ordinal());
		data.add((byte) range);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resistance * Math.pow(10, 6)), 4, true)));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
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
		return CalEnvironment.CalCode.ResistanceCode;
	}

	
	
	public WorkPattern getWorkPattern() {
		return workPattern;
	}

	public void setWorkPattern(WorkPattern workPattern) {
		this.workPattern = workPattern;
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

	public byte getResistanceIndex() {
		return resistanceIndex;
	}

	public void setResistanceIndex(byte resistanceIndex) {
		this.resistanceIndex = resistanceIndex;
	}

	
}
