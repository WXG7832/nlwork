package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 数据推送开关
 * @author Administrator
 *
 */
public class PushSwitchData extends Data implements Configable, Queryable, Responsable {

	private boolean open; // 推送开关

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) (open ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.PushSwitchCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
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
