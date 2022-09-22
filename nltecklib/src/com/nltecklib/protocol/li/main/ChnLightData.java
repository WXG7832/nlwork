package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData.ChannelLightData;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData.LightState;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月20日 下午2:34:47
* 通道灯控制协议
* 上位机通过分选或其他定义，给中位机发送通道灯控制命令
* 
*/
public class ChnLightData extends Data implements Configable, Responsable {
     
	private List<ChannelLightData>  lightList = new ArrayList<>();
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) lightList.size());
		for (int i = 0; i < lightList.size(); i++) {

			data.add((byte) lightList.get(i).getLightState().ordinal());
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;

		int lightCount = data.get(index++);// 解析通道灯数量

		for (int i = 0; i < lightCount; i++) {

			// new一个ChannelLightData
			ChannelLightData channelLightData = new ChannelLightData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LightState.values().length - 1) {

				throw new RuntimeException("error lightState code :" + code);
			}

			channelLightData.setLightState(LightState.values()[code]);// 解析通道灯状态
			channelLightData.setIndex(i + 1);// 加入显示通道灯序号

			lightList.add(channelLightData);// 加入channelLightDatas集合
		}


	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ChnLightCode;
	}

	@Override
	public String toString() {
		return "ChnLightData [lightList=" + lightList + "]";
	}

	public List<ChannelLightData> getLightList() {
		return lightList;
	}

	public void setLightList(List<ChannelLightData> lightList) {
		this.lightList = lightList;
	}
	
	
	
	
	

}
