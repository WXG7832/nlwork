package com.nltecklib.protocol.power.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月16日 下午1:59:03 驱动板基础保护协议
 */
public class DriverProtectData extends Data implements Configable, Queryable, Responsable {

	private double deviceVoltUpper;
	private double chnVoltUpper;
	private double chnVoltLower;
	private double chnCurrUpper;

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

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (deviceVoltUpper * Math.pow(10, 1)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnVoltUpper * Math.pow(10, 1)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnVoltLower * Math.pow(10, 1)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnCurrUpper * Math.pow(10, 2)), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		deviceVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnVoltLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnCurrUpper = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 100;
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.ProtectCode;
	}

	public double getDeviceVoltUpper() {
		return deviceVoltUpper;
	}

	public void setDeviceVoltUpper(double deviceVoltUpper) {
		this.deviceVoltUpper = deviceVoltUpper;
	}

	public double getChnVoltUpper() {
		return chnVoltUpper;
	}

	public void setChnVoltUpper(double chnVoltUpper) {
		this.chnVoltUpper = chnVoltUpper;
	}

	public double getChnVoltLower() {
		return chnVoltLower;
	}

	public void setChnVoltLower(double chnVoltLower) {
		this.chnVoltLower = chnVoltLower;
	}

	public double getChnCurrUpper() {
		return chnCurrUpper;
	}

	public void setChnCurrUpper(double chnCurrUpper) {
		this.chnCurrUpper = chnCurrUpper;
	}

	@Override
	public String toString() {
		return "DriverProtectData [deviceVoltUpper=" + deviceVoltUpper + ", chnVoltUpper=" + chnVoltUpper
				+ ", chnVoltLower=" + chnVoltLower + ", chnCurrUpper=" + chnCurrUpper + "]";
	}

}
