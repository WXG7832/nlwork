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

public class WorkformUpdateData extends Data implements Configable, Queryable, Responsable {

	private boolean update;// 0为升级模式，1为运行模式

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
		data.add((byte) (update ? 1 : 0));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (val != 1) {
			throw new RuntimeException("updateMode code error：" + val);
		}
		update = val == 1;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.WorkformUpdateCode;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	@Override
	public String toString() {
		return "WorkformUpdateData [update=" + update + "]";
	}

}
