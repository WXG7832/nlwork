package com.nltecklib.protocol.li.cal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalUpdateModeData extends Data implements Configable, Queryable, Responsable {

	private boolean updateMode;// 0为升级模式，1为运行模式

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

	@Override
	public void encode() {
		data.add((byte) driverIndex);
		data.add((byte) (updateMode ? 0 : 1));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (val > 1) {
			throw new RuntimeException("updateMode code error：" + val);
		}
		updateMode = val == 0;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.UpdateModeCode;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	@Override
	public String toString() {
		return "CalUpdateModeData [updateMode=" + updateMode + "]";
	}
	
	

}
