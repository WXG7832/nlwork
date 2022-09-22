package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月9日 上午11:03:53
* 校准板恒温开关调试
*/
public class CalTempControlDebugData extends Data implements Configable, Queryable, Responsable {
    
	private boolean open;//开启温控
	private int temperature;//恒温值
	private int upTemp;//设定恒温上限值
	private int lowTemp;//设定恒温下限值
	private int upTemper;//电子负载温度上限
	private long hotTime;//加热时长
	
	
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
		data.add((byte) (open ? 0x01 : 0x00)); 
		data.add((byte) temperature); 
		data.add((byte) upTemp); 
		data.add((byte) lowTemp); 
		data.add((byte) upTemper); 
		data.addAll(Arrays.asList(ProtocolUtil.split(hotTime, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		temperature = ProtocolUtil.getUnsignedByte(data.get(index++));
		upTemp = ProtocolUtil.getUnsignedByte(data.get(index++));
		lowTemp = ProtocolUtil.getUnsignedByte(data.get(index++));
		upTemper = ProtocolUtil.getUnsignedByte(data.get(index++));
		hotTime = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalTempControlDebugCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getUpTemp() {
		return upTemp;
	}

	public void setUpTemp(int upTemp) {
		this.upTemp = upTemp;
	}

	public int getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(int lowTemp) {
		this.lowTemp = lowTemp;
	}

	public int getUpTemper() {
		return upTemper;
	}

	public void setUpTemper(int upTemper) {
		this.upTemper = upTemper;
	}

	public long getHotTime() {
		return hotTime;
	}

	public void setHotTime(long hotTime) {
		this.hotTime = hotTime;
	}

	@Override
	public String toString() {
		return "CalTempControlDebugData [open=" + open + ", temperature=" + temperature + ", upTemp=" + upTemp
				+ ", lowTemp=" + lowTemp + ", upTemper=" + upTemper + ", hotTime=" + hotTime + "]";
	}
	
	

}
