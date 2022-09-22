package com.nltecklib.protocol.fuel.protect;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 保护板三色灯
 * 
 * @author caichao_tang
 *
 */
public class PBoardRGBLightData extends Data implements Configable, Responsable, Queryable {

	private int colorFlag;
	private int lightFlag;
	private int audioFlag;

	public static int GREEN_LIGHT = 1;
	public static int YELLOW_LIGHT = 1 << 1;
	public static int RED_LIGHT = 1 << 2;

	public int getColorFlag() {
		return colorFlag;
	}

	/**
	 * 设置三色灯
	 * @param colorFlag
	 * @see #GREEN_LIGHT
	 * @see #YELLOW_LIGHT
	 * @see #RED_LIGHT
	 */
	public void setColorFlag(int colorFlag) {
		this.colorFlag = colorFlag;
	}

	public int getLightFlag() {
		return lightFlag;
	}

	public void setLightFlag(int lightFlag) {
		this.lightFlag = lightFlag;
	}

	public int getAudioFlag() {
		return audioFlag;
	}

	public void setAudioFlag(int audioFlag) {
		this.audioFlag = audioFlag;
	}

	@Override
	public void encode() {
		data.add((byte) colorFlag);
		data.addAll(Arrays.asList(ProtocolUtil.split(lightFlag, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(audioFlag, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		int index = 0;
		data = encodeData;
		// 颜色标志位
		colorFlag = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 灯亮时间标志位
		lightFlag = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 蜂鸣器标志位
		audioFlag = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		return ProtectCode.RGB_LIGHT;
	}

	@Override
	public String toString() {
		return "PBoardRGBLightData [colorFlag=" + colorFlag + ", lightFlag=" + lightFlag + ", audioFlag=" + audioFlag
				+ "]";
	}

}
