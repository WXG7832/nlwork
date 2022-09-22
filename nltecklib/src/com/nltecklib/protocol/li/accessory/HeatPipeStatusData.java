package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class HeatPipeStatusData extends Data implements Configable, Queryable, Responsable {

	private boolean canOpen;



	public boolean isCanOpen() {
		return canOpen;
	}

	public void setCanOpen(boolean canOpen) {
		this.canOpen = canOpen;
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
		// TODO Auto-generated method stub
		data.add((byte) (canOpen ? 1 : 0));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		canOpen = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.HeatPipeStatusCode;
	}

	@Override
	public String toString() {
		return "HeatPipeStatusData [canOpen=" + canOpen + "]";
	}

	

}
