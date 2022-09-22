package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月8日 上午9:41:41
* unitIndex当做读取的校准模式，cc,cv,dc ； cc和DC模式返回的是表值是电流，需要*电阻系数
* 
*/
public class ReadMeterData extends Data implements Queryable, Responsable {
    
	private double  readVal;
	
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (readVal * Math.pow(10, 4)), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 万用表0.0001mV
		readVal = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, 4);
		index += 4;

	}
	
	
	

	public double getReadVal() {
		return readVal;
	}

	public void setReadVal(double readVal) {
		this.readVal = readVal;
	}
   
	
	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.ReadMeterCode;
	}

	@Override
	public String toString() {
		return "ReadMeterData [readVal=" + readVal + "]";
	}
	


}
