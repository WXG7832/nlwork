package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 通道使能开关
 * 
 * @author Administrator
 *
 */
public class CalChnEnableData extends Data implements Configable, Responsable, Queryable {

	private boolean open;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public boolean supportMain() {
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

		data.add((byte) (open ? 0x01 : 0x00));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
	}

	@Override
	public Code getCode() {
		return MainCode.CalChnEnableCode;
	}

	@Override
	public String toString() {
		return "CalChnEnableData [open=" + open + "]";
	}

	
	
}
