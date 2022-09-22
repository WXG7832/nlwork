package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;

/**
 * 
* @ClassName: PIEF44WaterWheelBatterySourceData  
* @Description: 下料机械手当前电芯对数（放到皮带上)
* @author zhang_longyong  
* @date 2019年12月16日
 */
public class PIEF44MechanicalArmCurrentBatteryData extends PlcData {
	
	private static final int DEFAULT_ADDRESS = 5056;
	private static final String DEFAULT_AREA = "DM";
	private int number;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public PIEF44MechanicalArmCurrentBatteryData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_ADDRESS;	//地址
		dataLength = 1;		// 默认长度
	}

	public byte[] encode(){
		String memory = area + "." + address + "." + dataLength;//内存地址
		byte[] data = null;
		if(!isRead){
			data = writeDataDecode();
		}
		return encode(memory, isBit, isRead, data);
	}

	@Override
	public byte[] writeDataDecode() {
		byte[] data = new byte[2];
		byte[] byteArray = intToByteArray(number);
		data[0] = byteArray[1];
		data[1] = byteArray[0];
		return data;
	}
	

	@Override
	public void decode(byte[] data) {
		if (data.length >= 2) {
			number = ((data[0] & 0x0ff) << 8) + (data[1] & 0x0ff);
		}
	}
}
