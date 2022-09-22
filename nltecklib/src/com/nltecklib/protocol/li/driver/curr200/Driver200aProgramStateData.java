package com.nltecklib.protocol.li.driver.curr200;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 驱动板程序状态查询 0x16 支持查询，不支持配置
 * @author Administrator
 */
public class Driver200aProgramStateData extends Data implements Queryable, Responsable {

	private boolean driverBurnOk;

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
		data.add((byte) (driverBurnOk ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverBurnOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	}

	@Override
	public Code getCode() {
		return DriverCode.ProgramStateCode;
	}

	public boolean isDriverBurnOk() {
		return driverBurnOk;
	}

	public void setDriverBurnOk(boolean driverBurnOk) {
		this.driverBurnOk = driverBurnOk;
	}

	@Override
	public String toString() {
		return "Driver200aProgramStateData [driverBurnOk=" + driverBurnOk + "]";
	}

}
