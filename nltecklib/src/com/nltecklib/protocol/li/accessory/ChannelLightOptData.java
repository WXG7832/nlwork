/**
 * 
 */
package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 批量操作通道指示灯 0x37
 * @author: JenHoard_Shaw
 * @date: 2022年5月31日 下午2:18:42
 *
 */
public class ChannelLightOptData extends Data implements Configable, Queryable, Responsable {

	private List<ChannelLightData> channelLightDatas = new ArrayList<>();// 所有通道指示灯数据集合

	/**
	 * @Description: 通道指示灯数据类
	 */
	public static class ChannelLightData {

		private int index = 0;// 通道号 从0开始显示
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
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
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
         
			data.add((byte) channelLightDatas.get(i).index);
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
			
			//设置通道序号
			channelLightData.setIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LightState.values().length - 1) {

				throw new RuntimeException("error lightState code :" + code);
			}

			channelLightData.setLightState(LightState.values()[code]);// 解析通道灯状态
			

			channelLightDatas.add(channelLightData);// 加入channelLightDatas集合
		}

	}

	@Override
	public Code getCode() {

		return AccessoryCode.ChannelLightCode;
	}

	public List<ChannelLightData> getChannelLightDatas() {
		return channelLightDatas;
	}

	public void setChannelLightDatas(List<ChannelLightData> channelLightDatas) {
		this.channelLightDatas = channelLightDatas;
	}

}
