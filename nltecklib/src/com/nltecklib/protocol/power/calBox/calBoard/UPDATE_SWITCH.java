package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class UPDATE_SWITCH extends Data implements Configable, Queryable, Responsable {

    private boolean enabled = false; // ∆Ù”√

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
	data.add((byte) (enabled ? 0 : 1));

    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	int val = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (val > 1) {
	    throw new RuntimeException("UPDATE_SWITCH code error£∫" + val);
	}
	enabled = val == 0;
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalBoardCode.UPDATE_SWITCH;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

}
