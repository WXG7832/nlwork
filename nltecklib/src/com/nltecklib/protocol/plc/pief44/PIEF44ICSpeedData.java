package com.nltecklib.protocol.plc.pief44;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.model.Speed;

/**
 *化成夹具相关的速度的地址
 * @author Administrator
 *
 */
public class PIEF44ICSpeedData extends PlcData{
	
	private static final int DEFAULT_IC_ADDRESS = 3512;
	private static final String DEFAULT_AREA = "DM";
	private List<Speed> speeds = new ArrayList<Speed>();

	public List<Speed> getSpeeds() {
		return speeds;
	}

	public void setSpeeds(List<Speed> speeds) {
		this.speeds = speeds;
	}

	public byte[] encode(){
		
		String memory = area + "." + (address + fixtureIndex * 20) + "." + dataLength * 20;//内存地址
		byte[] data = writeDataDecode();
		return encode(memory, isBit, isRead, data);
	}
	
	public PIEF44ICSpeedData() {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		dataLength = 4;	// 默认读写数据长度
	}
	
	public PIEF44ICSpeedData(int fixtureIndex, int dataLength, boolean isRead) {
		super();
		area = DEFAULT_AREA;		// 默认地址区域
		address = DEFAULT_IC_ADDRESS;	//地址
		this.fixtureIndex = fixtureIndex;
		this.dataLength = dataLength;
		this.isRead = isRead;
	}
	
	@Override
	public byte[] writeDataDecode() {
		byte[] data = null;
		if(!isRead && !isBit){
			data = new byte[dataLength * 40];
			for (int i = 0; i < speeds.size(); i++) {
				Speed speed = speeds.get(i);
				byte[] preByteArray = floatToByteArray(speed.getPrePress());
				data[i * 40] = preByteArray[1];
				data[i * 40 + 1] = preByteArray[0];
				data[i * 40 + 2] = preByteArray[3];
				data[i * 40 + 3] = preByteArray[2];
				
				byte[] jogByteArray = intToByteArray(speed.getJogSpeed());
				data[i * 40 + 4] = jogByteArray[1];
				data[i * 40 + 5] = jogByteArray[0];
				data[i * 40 + 6] = jogByteArray[3];
				data[i * 40 + 7] = jogByteArray[2];
				
				byte[] firByteArray = intToByteArray(speed.getFirstSpeed());
				data[i * 40 + 8] = firByteArray[1];
				data[i * 40 + 9] = firByteArray[0];
				data[i * 40 + 10] = firByteArray[3];
				data[i * 40 + 11] = firByteArray[2];
				
				byte[] senByteArray = intToByteArray(speed.getSecondSpeed());
				data[i * 40 + 12] = senByteArray[1];
				data[i * 40 + 13] = senByteArray[0];
				data[i * 40 + 14] = senByteArray[3];
				data[i * 40 + 15] = senByteArray[2];
				
				byte[] thiByteArray = intToByteArray(speed.getThirdSpeed());
				data[i * 40 + 16] = thiByteArray[1];
				data[i * 40 + 17] = thiByteArray[0];
				data[i * 40 + 18] = thiByteArray[3];
				data[i * 40 + 19] = thiByteArray[2];
				
				byte[] fouByteArray = intToByteArray(speed.getFourthSpeed());
				data[i * 40 + 20] = fouByteArray[1];
				data[i * 40 + 21] = fouByteArray[0];
				data[i * 40 + 22] = fouByteArray[3];
				data[i * 40 + 23] = fouByteArray[2];
				
				byte[] fifByteArray = intToByteArray(speed.getFifthSpeed());
				data[i * 40 + 24] = fifByteArray[1];
				data[i * 40 + 25] = fifByteArray[0];
				data[i * 40 + 26] = fifByteArray[3];
				data[i * 40 + 27] = fifByteArray[2];
				
				byte[] retByteArray = intToByteArray(speed.getReturnSpeed());
				data[i * 40 + 28] = retByteArray[1];
				data[i * 40 + 29] = retByteArray[0];
				data[i * 40 + 30] = retByteArray[3];
				data[i * 40 + 31] = retByteArray[2];

				byte[] comByteArray = intToByteArray(speed.getCompansateSpeed());
				data[i * 40 + 32] = comByteArray[1];
				data[i * 40 + 33] = comByteArray[0];
				data[i * 40 + 34] = comByteArray[3];
				data[i * 40 + 35] = comByteArray[2];
				
				byte[] presByteArray = intToByteArray(speed.getPressChangeSpeed());
				data[i * 40 + 36] = presByteArray[1];
				data[i * 40 + 37] = presByteArray[0];
				data[i * 40 + 38] = presByteArray[3];
				data[i * 40 + 39] = presByteArray[2];
				
			}
		}
		System.out.println(data);
		return data;
	}
	

	@Override
	public void decode(byte[] data) {
		for (int i = 0; i < data.length; i += 40) {
			Speed speed = new Speed();
			
			int prePress = ((data[i] & 0x0ff) << 8) + (data[i + 1] & 0x0ff) + 
					((data[i + 2] & 0x0ff) << 24) + ((data[i + 3] & 0x0ff) << 16);
			speed.setPrePress(Float.intBitsToFloat(prePress));
			int jogSpeed = ((data[i + 4] & 0x0ff) << 8) + (data[i + 5] & 0x0ff) + 
					((data[i + 6] & 0x0ff) << 24) + ((data[i + 7] & 0x0ff) << 16);
			speed.setJogSpeed(jogSpeed);
			int firstSpeed = ((data[i + 8] & 0x0ff) << 8) + (data[i + 9] & 0x0ff) + 
					((data[i + 10] & 0x0ff) << 24) + ((data[i + 11] & 0x0ff) << 16);
			speed.setFirstSpeed(firstSpeed);
			int sencondSpeed = ((data[i + 12] & 0x0ff) << 8) + (data[i + 13] & 0x0ff) + 
					((data[i + 14] & 0x0ff) << 24) + ((data[i + 15] & 0x0ff) << 16);
			speed.setSecondSpeed(sencondSpeed);
			int thirdSpeed = ((data[i + 16] & 0x0ff) << 8) + (data[i + 17] & 0x0ff) + 
					((data[i + 18] & 0x0ff) << 24) + ((data[i + 19] & 0x0ff) << 16);
			speed.setThirdSpeed(thirdSpeed);
			int fourSpeed = ((data[i + 20] & 0x0ff) << 8) + (data[i + 21] & 0x0ff) + 
					((data[i + 22] & 0x0ff) << 24) + ((data[i + 23] & 0x0ff) << 16);
			speed.setFourthSpeed(fourSpeed);
			int fifthSpeed = ((data[i + 24] & 0x0ff) << 8) + (data[i + 25] & 0x0ff) + 
					((data[i + 26] & 0x0ff) << 24) + ((data[i + 27] & 0x0ff) << 16);
			speed.setFifthSpeed(fifthSpeed);
			int retSpeed = ((data[i + 28] & 0x0ff) << 8) + (data[i + 29] & 0x0ff) + 
					((data[i + 30] & 0x0ff) << 24) + ((data[i + 31] & 0x0ff) << 16);
			speed.setReturnSpeed(retSpeed);
			int comSpeed = ((data[i + 32] & 0x0ff) << 8) + (data[i + 33] & 0x0ff) + 
					((data[i + 34] & 0x0ff) << 24) + ((data[i + 35] & 0x0ff) << 16);
			speed.setCompansateSpeed(comSpeed);
			int presSpeed = ((data[i + 36] & 0x0ff) << 8) + (data[i + 37] & 0x0ff) + 
					((data[i + 38] & 0x0ff) << 24) + ((data[i + 39] & 0x0ff) << 16); 
			speed.setPressChangeSpeed(presSpeed);
			speeds.add(speed);
		}
	}
	
}
