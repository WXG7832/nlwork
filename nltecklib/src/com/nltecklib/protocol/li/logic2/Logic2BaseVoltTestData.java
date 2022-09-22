package com.nltecklib.protocol.li.logic2;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月24日 下午1:40:49
* 驱动板基准电压，温度测试协议
*/
public class Logic2BaseVoltTestData extends Data implements Queryable, Responsable {
   
	private int   driverTemp1;
	private int   driverTemp2;
	private double  driverBaseVolt;
	private boolean  isDriverBaseVoltOK;
	
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) driverTemp1);
		data.add((byte) driverTemp2);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (driverBaseVolt * 10), 2, true)));
		data.add((byte) (isDriverBaseVoltOK ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverTemp1 = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverTemp2 = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverBaseVolt = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		isDriverBaseVoltOK = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.BaseVoltTestCode;
	}

	public int getDriverTemp1() {
		return driverTemp1;
	}

	public void setDriverTemp1(int driverTemp1) {
		this.driverTemp1 = driverTemp1;
	}

	public int getDriverTemp2() {
		return driverTemp2;
	}

	public void setDriverTemp2(int driverTemp2) {
		this.driverTemp2 = driverTemp2;
	}

	public double getDriverBaseVolt() {
		return driverBaseVolt;
	}

	public void setDriverBaseVolt(double driverBaseVolt) {
		this.driverBaseVolt = driverBaseVolt;
	}
	
	

	public boolean isDriverBaseVoltOK() {
		return isDriverBaseVoltOK;
	}

	public void setDriverBaseVoltOK(boolean isDriverBaseVoltOK) {
		this.isDriverBaseVoltOK = isDriverBaseVoltOK;
	}
	
	

}
