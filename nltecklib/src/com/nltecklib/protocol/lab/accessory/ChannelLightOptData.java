/**
 * 
 */
package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 通道指示灯操作 0x19 支持配置、查询
 * @author: JenHoard_Shaw
 * @date: 2022年5月16日 下午4:05:15
 *
 */
public class ChannelLightOptData extends Data implements Configable, Queryable, Responsable {

	private List<ChannelLightData> channelLightDatas = new ArrayList<>();// 所有通道指示灯数据集合

	/**
	 * @Description: 通道指示灯数据类
	 */
	public static class ChannelLightData {

		private int index;// 通道号
		private LightState lightState = LightState.CLOSE;// 通道灯状态

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public LightState getLightState() {
			return lightState;
		}

		public void setLightState(LightState lightState) {
			this.lightState = lightState;
		}

	}

	/**
	 * @Description: 通道灯状态枚举类 通道灯状态： 0-关闭 , 1-R, 2-G, 3-RG, 4-B, 5-RB, 6-GB, 7-RGB
	 */
	public enum LightState {

		CLOSE, R, G, RG, B, RB, GB, RGB
	}

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) channelLightDatas.size());
		for (int i = 0; i < channelLightDatas.size(); i++) {

			data.add((byte) channelLightDatas.get(i).lightState.ordinal());
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

			channelLightDatas.add(channelLightData);// 加入channelLightDatas集合
		}

	}

	@Override
	public Code getCode() {
		return AccessoryCode.ChannelLightCode;// 功能码
	}

	// get和set方法
	public List<ChannelLightData> getChannelLightDatas() {
		return channelLightDatas;
	}

	public void setChannelLightDatas(List<ChannelLightData> channelLightDatas) {
		this.channelLightDatas = channelLightDatas;
	}

}
