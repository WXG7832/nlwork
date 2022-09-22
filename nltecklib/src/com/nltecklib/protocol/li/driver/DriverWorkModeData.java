package com.nltecklib.protocol.li.driver;

import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 工作模式配置
 * 
 * @author admin
 */
public class DriverWorkModeData extends Data implements Configable, Queryable, Responsable {

    private WorkType workType;

    public enum WorkType {

	UDT(0x00), WORK(0x01), CAL(0x05), UPGRADE(0x06);

	private int code;

	private WorkType(int code) {

	    this.code = code;
	}

	public int getCode() {

	    return code;
	}

	public static WorkType valuesOf(int code) {

	    switch (code) {
	    case 0:
		return UDT;
	    case 1:
		return WORK;
	    case 5:
		return CAL;
	    case 6:
		return UPGRADE;
	    }

	    return null;
	}
    }

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
	data.add((byte) workType.getCode());
    }

    @Override
    public void decode(List<Byte> data) {

	int index = 0;

	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	for (WorkType mode : WorkType.values()) {
	    if (mode.getCode() == code) {
		workType = mode;
		break;
	    } else if (mode == WorkType.values()[WorkType.values().length - 1]) {
		throw new RuntimeException("error work type code : " + code);

	    }
	}
    }

    @Override
    public Code getCode() {
	return DriverCode.WorkModeCode;
    }

    public WorkType getWorkType() {
	return workType;
    }

    public void setWorkType(WorkType workType) {
	this.workType = workType;
    }

}
