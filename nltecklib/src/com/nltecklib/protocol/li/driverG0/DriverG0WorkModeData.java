package com.nltecklib.protocol.li.driverG0;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.util.ProtocolUtil;

public class DriverG0WorkModeData extends Data implements Configable, Queryable, Responsable {

    private DriverG0WorkType driverG0WorkType;

    public enum DriverG0WorkType {

	IAP_MODE(0x00), APP_MODE(0x01);

	private int code;

	private DriverG0WorkType(int code) {
	    this.code = code;
	}

	public int getCode() {
	    return code;
	}

    }

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
	data.add((byte) driverG0WorkType.getCode());
    }

    @Override
    public void decode(List<Byte> data) {
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (code > DriverG0WorkType.values().length - 1) {
	    throw new RuntimeException("error work type code : " + code);
	}
	driverG0WorkType = DriverG0WorkType.values()[code];
    }

    @Override
    public Code getCode() {
	return DriverG0Code.CHIP_WORK_MODE;
    }

    public DriverG0WorkType getDriverG0WorkType() {
	return driverG0WorkType;
    }

    public void setDriverG0WorkType(DriverG0WorkType driverG0WorkType) {
	this.driverG0WorkType = driverG0WorkType;
    }

}
