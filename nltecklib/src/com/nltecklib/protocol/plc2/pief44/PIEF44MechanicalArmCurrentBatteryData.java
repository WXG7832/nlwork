package com.nltecklib.protocol.plc2.pief44;

import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.plc2.PlcData;


/**
 * 
* @ClassName: PIEF44WaterWheelBatterySourceData  
* @Description: 下料机械手当前电芯对数（放到皮带上)
* @author zhang_longyong  
* @date 2019年12月16日
 */
public class PIEF44MechanicalArmCurrentBatteryData extends PlcData {
	
	private static final int DEFAULT_ADDRESS = 5056;
	private int number;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public PIEF44MechanicalArmCurrentBatteryData() {
		super();
		area = Area.DM;		// 默认地址区域
	}

	public void encode(){
		address = DEFAULT_ADDRESS;
		
		if(orient == Orient.WRITE){
			byte[] byteArray = intToByteArray(number);
			data.add(byteArray[1]);
			data.add(byteArray[0]);
		}
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		if (data.size() >= 2) {
			number = ((data.get(0) & 0x0ff) << 8) + (data.get(1) & 0x0ff);
		}
	}
}
