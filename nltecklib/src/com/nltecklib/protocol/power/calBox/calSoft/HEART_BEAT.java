package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class HEART_BEAT extends Data implements Queryable, Responsable {

    private boolean deviceConnected = false;
    private boolean meterConnected = false;

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

	data.add((byte) (deviceConnected ? 1 : 0));
	data.add((byte) (meterConnected ? 1 : 0));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	// TODO Auto-generated method stub

	data = encodeData;
	int index = 0;
	deviceConnected = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	meterConnected = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.HEART_BEAT;
    }

    public boolean isDeviceConnected() {
	return deviceConnected;
    }

    public void setDeviceConnected(boolean deviceConnected) {
	this.deviceConnected = deviceConnected;
    }

    public boolean isMeterConnected() {
	return meterConnected;
    }

    public void setMeterConnected(boolean meterConnected) {
	this.meterConnected = meterConnected;
    }

}
