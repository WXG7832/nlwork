package com.nltecklib.protocol.li.driver.curr200;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 读取驱动板单片机ID号  0x12  支持读取
 *@author Administrator
 */
public class Driver200aChipIDData extends Data implements Queryable, /*Configable,*/ Responsable {
    private static final int CHIPID_LENGTH = 12;
    private String chipid;

    @Override
    public boolean supportUnit() {
	return false;
    }

    @Override
    public boolean supportDriver() {
	return true;
    }

    @Override
    public boolean supportChannel() {
	return false;
    }

    @Override
    public void encode() {
	data.add((byte) driverIndex);
	int index = 0;
	for (int i = 0; i < CHIPID_LENGTH; i++) {
	    data.add((byte) Integer.parseInt(chipid.substring(index, index = index + 2), 16));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	StringBuilder stringBuilder = new StringBuilder();
	for (int i = 0; i < CHIPID_LENGTH; i++) {
	    String string = Integer.toHexString(ProtocolUtil.getUnsignedByte(data.get(index++)));
	    if (string.length() < 2) {
		string = "0" + string;
	    }
	    stringBuilder.append(string);
	}
	chipid = stringBuilder.toString();
    }

    @Override
    public Code getCode() {
	return DriverCode.DRIVER_CHIPID;
    }

    public String getChipid() {
	return chipid;
    }

    public void setChipid(String chipid) {
	this.chipid = chipid;
    }

}
