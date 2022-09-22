package com.nltecklib.protocol.plc;

import java.util.HashMap;

import com.nltecklib.protocol.fins.FinsData;

public abstract class PlcData extends FinsData{
	
	protected int fixtureIndex;			// 夹具号
	
	/**
	 * 将获取到的byte[]转换成Map
	 * @param data
	 * @return
	 */
	@Override
	public void decode(byte[] data){
		
		datas = new HashMap<>();
		int length = data.length / 2;	// 一个通道地址 占2个byte
		int index = 0;
		
		for (int i = 0; i < length; i++) {
			
			int value = ((data[index] & 0x0ff) << 8) + (data[index + 1] & 0x0ff);
			datas.put(fixtureIndex + i, value);
			index = index + 2;
		}
		
	}
	
	/**
	 * 对写入的数据进行处理(整形)
	 * @return
	 */
	public byte[] writeDataDecode(){
		byte[] data = null;
		if(!isRead && !isBit){
			data = new byte[dataLength * 2];
			for (int i = 0; i < dataLength; i++) {
				int value = datas.get(i + fixtureIndex);
				byte[] byteArray = intToByteArray(value);
				data[i*2] = byteArray[1];
				data[i*2+1] = byteArray[0];
			}
		}
		return data;
	}

	public int getFixtureIndex() {
		return fixtureIndex;
	}

	public void setFixtureIndex(int fixtureIndex) {
		this.fixtureIndex = fixtureIndex;
	}

	
}
