package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月23日 下午4:17:41 三色灯控制协议
 */
public class ColorLightData extends Data implements Configable, Responsable, Queryable {

	public enum LightColor {

		OFF(0X00) , GREEN(0x01), YELLOW(0X02), RED(0X04) , FLU(0X08);

		private LightColor(int code) {

			this.code = code;
		}

		private int code;

		public int getCode() {
			return code;
		}

		public static LightColor valueOf(int code) {

			switch (code) {

			case 1:
				return GREEN;
			case 2:
				return YELLOW;
			case 4:
				return RED;
			}

			return null;
		}

	}

	private LightColor color;
	private short flag;

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
		data.add((byte) color.getCode());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (flag), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 颜色标志位
		color = LightColor.valueOf(ProtocolUtil.getUnsignedByte(data.get(index++)));
		// 灯亮时间标志位
		flag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return AccessoryCode.ColorLightCode;
	}

	public LightColor getColor() {
		return color;
	}

	public void setColor(LightColor color) {
		this.color = color;
	}

	public short getFlag() {
		return flag;
	}

	public void setFlag(short flag) {
		this.flag = flag;
	}
	
	

}
