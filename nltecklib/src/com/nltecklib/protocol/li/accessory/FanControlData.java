package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.util.ProtocolUtil;


public class FanControlData extends Data implements Configable, Queryable, Responsable {
    
	private Direction    direction  = Direction.IN;
	private PowerState   powerState = PowerState.OFF;
	private int          grade      = 9;  //风速等级;默认最大风力9
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public boolean supportDriver() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) direction.ordinal());
		data.add((byte) powerState.ordinal());
		data.add((byte) grade);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Direction.values().length) {

			throw new RuntimeException("error direction code " + flag);
		}
		direction = Direction.values()[flag];
		
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= PowerState.values().length) {

			throw new RuntimeException("error power state code " + flag);
		}
		powerState = PowerState.values()[flag];
		
		grade = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.FanControlCode;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public PowerState getPowerState() {
		return powerState;
	}

	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	
	

}
