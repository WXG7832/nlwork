package com.nltecklib.protocol.plc2.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.plc2.PlcData;
import com.nltecklib.protocol.plc2.pief44.model.Speed;


/**
 *化成夹具相关的速度的地址
 * @author Administrator
 *
 */
public class PIEF44ICSpeedData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 3512;
	private List<Speed> speeds = new ArrayList<Speed>();

	public List<Speed> getSpeeds() {
		return speeds;
	}

	public void setSpeeds(List<Speed> speeds) {
		this.speeds = speeds;
	}

	public void encode(){
		data.clear();
		address = DEFAULT_IC_ADDRESS + fixtureIndex * 20;
		
		for (int i = 0; i < speeds.size(); i++) {
			Speed speed = speeds.get(i);
			byte[] preByteArray = floatToByteArray(speed.getPrePress());
			data.add(preByteArray[1]);
			data.add(preByteArray[0]);
			data.add(preByteArray[3]);
			data.add(preByteArray[2]);
			
			byte[] jogByteArray = intToByteArray(speed.getJogSpeed());
			data.add(jogByteArray[1]);
			data.add(jogByteArray[0]);
			data.add(jogByteArray[3]);
			data.add(jogByteArray[2]);
			
			byte[] firByteArray = intToByteArray(speed.getFirstSpeed());
			data.add(firByteArray[1]);
			data.add(firByteArray[0]);
			data.add(firByteArray[3]);
			data.add(firByteArray[2]);
			
			byte[] senByteArray = intToByteArray(speed.getSecondSpeed());
			data.add(senByteArray[1]);
			data.add(senByteArray[0]);
			data.add(senByteArray[3]);
			data.add(senByteArray[2]);
			
			byte[] thiByteArray = intToByteArray(speed.getThirdSpeed());
			data.add(thiByteArray[1]);
			data.add(thiByteArray[0]);
			data.add(thiByteArray[3]);
			data.add(thiByteArray[2]);
			
			byte[] fouByteArray = intToByteArray(speed.getFourthSpeed());
			data.add(fouByteArray[1]);
			data.add(fouByteArray[0]);
			data.add(fouByteArray[3]);
			data.add(fouByteArray[2]);
			
			byte[] fifByteArray = intToByteArray(speed.getFifthSpeed());
			data.add(fifByteArray[1]);
			data.add(fifByteArray[0]);
			data.add(fifByteArray[3]);
			data.add(fifByteArray[2]);
			
			byte[] retByteArray = intToByteArray(speed.getReturnSpeed());
			data.add(retByteArray[1]);
			data.add(retByteArray[0]);
			data.add(retByteArray[3]);
			data.add(retByteArray[2]);

			byte[] comByteArray = intToByteArray(speed.getCompansateSpeed());
			data.add(comByteArray[1]);
			data.add(comByteArray[0]);
			data.add(comByteArray[3]);
			data.add(comByteArray[2]);
			
			byte[] presByteArray = intToByteArray(speed.getPressChangeSpeed());
			data.add(presByteArray[1]);
			data.add(presByteArray[0]);
			data.add(presByteArray[3]);
			data.add(presByteArray[2]);
			
		}
	}
	
	public PIEF44ICSpeedData() {
		super();
		area = Area.DM;		// 默认地址区域
	}

	@Override
	public void decode(List<Byte> encodeData) {
		speeds.clear();
		data = encodeData;
		for (int i = 0; i < data.size(); i += 40) {
			Speed speed = new Speed();
			
			int prePress = ((data.get(i) & 0x0ff) << 8) + (data.get(i + 1) & 0x0ff) + 
					((data.get(i + 2) & 0x0ff) << 24) + ((data.get(i + 3) & 0x0ff) << 16);
			speed.setPrePress(Float.intBitsToFloat(prePress));
			int jogSpeed = ((data.get(i + 4) & 0x0ff) << 8) + (data.get(i + 5) & 0x0ff) + 
					((data.get(i + 6) & 0x0ff) << 24) + ((data.get(i + 7) & 0x0ff) << 16);
			speed.setJogSpeed(jogSpeed);
			int firstSpeed = ((data.get(i + 8) & 0x0ff) << 8) + (data.get(i + 9) & 0x0ff) + 
					((data.get(i + 10) & 0x0ff) << 24) + ((data.get(i + 11) & 0x0ff) << 16);
			speed.setFirstSpeed(firstSpeed);
			int sencondSpeed = ((data.get(i + 12) & 0x0ff) << 8) + (data.get(i + 13) & 0x0ff) + 
					((data.get(i + 14) & 0x0ff) << 24) + ((data.get(i + 15) & 0x0ff) << 16);
			speed.setSecondSpeed(sencondSpeed);
			int thirdSpeed = ((data.get(i + 16) & 0x0ff) << 8) + (data.get(i + 17) & 0x0ff) + 
					((data.get(i + 18) & 0x0ff) << 24) + ((data.get(i + 19) & 0x0ff) << 16);
			speed.setThirdSpeed(thirdSpeed);
			int fourSpeed = ((data.get(i + 20) & 0x0ff) << 8) + (data.get(i + 21) & 0x0ff) + 
					((data.get(i + 22) & 0x0ff) << 24) + ((data.get(i + 23) & 0x0ff) << 16);
			speed.setFourthSpeed(fourSpeed);
			int fifthSpeed = ((data.get(i + 24) & 0x0ff) << 8) + (data.get(i + 25) & 0x0ff) + 
					((data.get(i + 26) & 0x0ff) << 24) + ((data.get(i + 27) & 0x0ff) << 16);
			speed.setFifthSpeed(fifthSpeed);
			int retSpeed = ((data.get(i + 28) & 0x0ff) << 8) + (data.get(i + 29) & 0x0ff) + 
					((data.get(i + 30) & 0x0ff) << 24) + ((data.get(i + 31) & 0x0ff) << 16);
			speed.setReturnSpeed(retSpeed);
			int comSpeed = ((data.get(i + 32) & 0x0ff) << 8) + (data.get(i + 33) & 0x0ff) + 
					((data.get(i + 34) & 0x0ff) << 24) + ((data.get(i + 35) & 0x0ff) << 16);
			speed.setCompansateSpeed(comSpeed);
			int presSpeed = ((data.get(i + 36) & 0x0ff) << 8) + (data.get(i + 37) & 0x0ff) + 
					((data.get(i + 38) & 0x0ff) << 24) + ((data.get(i + 39) & 0x0ff) << 16); 
			speed.setPressChangeSpeed(presSpeed);
			speeds.add(speed);
		}
	}
	
}
