package com.nltecklib.protocol.power.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 下午4:04:12
* 通道操作协议
*/
public class DriverOperateData extends Data implements Configable, Responsable {
   
	private int  optFlag; //操作标记位，1代表选中，0代表未选中
	
	private boolean open; //打开通道?
	
	
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
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)optFlag, Data.isUseHugeDriverChnCount() ? 4 : 2, true)));
		data.add((byte) (open ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		optFlag = (int) ProtocolUtil.compose(data.subList(index, index + (Data.isUseHugeDriverChnCount() ? 4 : 2)).toArray(new Byte[0]), true);
		index += Data.isUseHugeDriverChnCount() ? 4 : 2;
		open = data.get(index++) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.OperateCode;
	}

	public int getOptFlag() {
		return optFlag;
	}

	public void setOptFlag(int optFlag) {
		this.optFlag = optFlag;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public String toString() {
		return "OperateData [optFlag=" + optFlag + ", open=" + open + "]";
	}
	
	
	
	
	

}
