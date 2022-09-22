package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月4日 下午4:50:31
* 查询设备主控的基础信息
*/
public class MbBaseInfoData extends Data implements Queryable, Responsable {
    
	private int    driverCount;
	private long   enableFlag; /*通过位表示驱动板启用状态*/
	
	
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
		
		data.add((byte) driverCount);
		data.addAll(Arrays.asList(ProtocolUtil.split(enableFlag, 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		driverCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		enableFlag = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
		
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalBoxDeviceCode.BaseInfoCode;
	}

	public int getDriverCount() {
		return driverCount;
	}

	public void setDriverCount(int driverCount) {
		this.driverCount = driverCount;
	}

	public long getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(long enableFlag) {
		this.enableFlag = enableFlag;
	}

	@Override
	public String toString() {
		return "MbBaseInfoData [driverCount=" + driverCount + ", enableFlag=" + enableFlag + "]";
	}
	
	
	

}
