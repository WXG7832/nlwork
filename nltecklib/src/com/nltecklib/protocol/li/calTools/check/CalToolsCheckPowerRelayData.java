package com.nltecklib.protocol.li.calTools.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsCheckPowerRelayData extends Data implements Configable, Queryable, Responsable {

	private boolean open;
	

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
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

	@Override
	public void encode() {
		data.add((byte) (open?1:0));
	}


	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		open=ProtocolUtil.getUnsignedByte(data.get(index++))==1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsCheckCode.PowerRelayCode;
	}

	@Override
	public String toString() {
		return "CalToolsCheckPowerRelayData [open=" + open + "]";
	}

}
