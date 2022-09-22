package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BindCalBoardData extends Data implements Configable, Responsable {

	private int calIndex;
	private boolean bind;// 1°ó¶¨0½â°ó

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
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) calIndex);
		data.add((byte) (bind ? 1 : 0));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		calIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (val > 1) {
			throw new RuntimeException("bind code error£º" + val);
		}
		bind = val == 1;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.BindCalBoardCode;
	}

	public int getCalIndex() {
		return calIndex;
	}

	public void setCalIndex(int calIndex) {
		this.calIndex = calIndex;
	}

	public boolean isBind() {
		return bind;
	}

	public void setBind(boolean bind) {
		this.bind = bind;
	}

	@Override
	public String toString() {
		return "BindCalBoardData [calIndex=" + calIndex + ", bind=" + bind + "]";
	}

}
