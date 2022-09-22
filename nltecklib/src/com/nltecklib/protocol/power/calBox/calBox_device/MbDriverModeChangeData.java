package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.WorkMode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月28日 下午3:40:57
* 带驱动板序号的工作模式切换协议
*/
public class MbDriverModeChangeData extends Data implements Configable, Queryable, Responsable {
   
	private WorkMode  mode = WorkMode.NORMAL;
	
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
		
		data.add((byte) mode.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		int code = data.get(index++);
		if(code > WorkMode.values().length - 1) {
			
			throw new RuntimeException("error workmode code:" + code);
		}
		mode = WorkMode.values()[code];

	}


	public WorkMode getMode() {
		return mode;
	}

	public void setMode(WorkMode mode) {
		this.mode = mode;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalBoxDeviceCode.DriverModeCode;
	}

	@Override
	public String toString() {
		return "MbDriverModeChangeData [mode=" + mode + ", driverIndex=" + driverIndex + "]";
	}
	
	

}
