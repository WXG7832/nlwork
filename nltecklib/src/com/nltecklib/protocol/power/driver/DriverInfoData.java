package com.nltecklib.protocol.power.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 下午2:56:48
* 类说明
*/
public class DriverInfoData extends Data implements Queryable, Responsable {
   
	private int  chnCount;
	private double  maxCurrent;
	private String  driverSoftVersion;
	private String  checkSoftVersion;
	private String  pickSoftVersion;
	private String  tempSoftVersion;
	
	
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
		
        data.add((byte) chnCount);
        data.addAll(Arrays.asList(ProtocolUtil.split((long)maxCurrent, 2, true)));
        data.addAll(ProtocolUtil.encodeString(driverSoftVersion, "US-ASCII", 30));
        data.addAll(ProtocolUtil.encodeString(checkSoftVersion, "US-ASCII", 30));
        data.addAll(ProtocolUtil.encodeString(pickSoftVersion, "US-ASCII", 30));
        data.addAll(ProtocolUtil.encodeString(tempSoftVersion, "US-ASCII", 30));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		chnCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		maxCurrent = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		driverSoftVersion = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;
		checkSoftVersion = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;
		pickSoftVersion = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;
		tempSoftVersion = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.InfoCode;
	}

	public int getChnCount() {
		return chnCount;
	}

	public void setChnCount(int chnCount) {
		this.chnCount = chnCount;
	}

	public double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public String getDriverSoftVersion() {
		return driverSoftVersion;
	}

	public void setDriverSoftVersion(String driverSoftVersion) {
		this.driverSoftVersion = driverSoftVersion;
	}

	public String getCheckSoftVersion() {
		return checkSoftVersion;
	}

	public void setCheckSoftVersion(String checkSoftVersion) {
		this.checkSoftVersion = checkSoftVersion;
	}

	public String getPickSoftVersion() {
		return pickSoftVersion;
	}

	public void setPickSoftVersion(String pickSoftVersion) {
		this.pickSoftVersion = pickSoftVersion;
	}

	public String getTempSoftVersion() {
		return tempSoftVersion;
	}

	public void setTempSoftVersion(String tempSoftVersion) {
		this.tempSoftVersion = tempSoftVersion;
	}

	@Override
	public String toString() {
		return "DriverInfoData [chnCount=" + chnCount + ", maxCurrent=" + maxCurrent + ", driverSoftVersion="
				+ driverSoftVersion + ", checkSoftVersion=" + checkSoftVersion + ", pickSoftVersion=" + pickSoftVersion
				+ ", tempSoftVersion=" + tempSoftVersion + "]";
	}
	
	

}
