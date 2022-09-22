package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.ControlType;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;

public class DebugControlData extends Data implements Configable,Responsable{
    
	
	
	private ControlType type;
	private boolean open;
	
	@Override
	public void encode() {
		
		data.add((byte) type.ordinal());
		data.add((byte) (open ? 1 : 0));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
        data = encodeData;
		int index = 0;
		type = ControlType.values()[data.get(index++)];
		open = data.get(index++) == 1;
	}

	@Override
	public Code getCode() {
		
		return MainCode.DebugControlCode;
	}

	public ControlType getControlType() {
		return type;
	}

	public void setControlType(ControlType type) {
		this.type = type;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
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
