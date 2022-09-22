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
 * 
 * @Description: 四色灯及报警 0x39 支持配置，支持读取
 * @author: JenHoard_Shaw
 * @date: 2022年6月16日 下午1:43:08
 *
 */
public class FourLightStateData extends Data implements Configable, Queryable, Responsable {

	private int lightState;
	private int blinkState;
	private int buzzerState;

	@Override
	public boolean supportUnit() {
		return true;
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

		data.add((byte) unitIndex);
		data.add((byte) lightState);
		data.addAll(Arrays.asList(ProtocolUtil.split(blinkState, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(buzzerState, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		lightState = ProtocolUtil.getUnsignedByte(data.get(index++));
		blinkState = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		buzzerState = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public String toString() {

		StringBuilder light = new StringBuilder();

		if ((lightState >> 3 & 1) == 1) {
			light.append("蓝灯：开；");
		} else {
			light.append("蓝灯：关；");
		}
		if ((lightState >> 2 & 1) == 1) {
			light.append("黄灯：开；");
		} else {
			light.append("黄灯：关；");
		}
		if ((lightState >> 1 & 1) == 1) {
			light.append("绿灯：开；");
		} else {
			light.append("绿灯：关；");
		}
		if ((lightState >> 0 & 1) == 1) {
			light.append("红灯：开");
		} else {
			light.append("红灯：关");
		}

		return "四色灯及报警 [地址=" + (unitIndex + 1) + ", lightState=" + light + ", 闪烁标志位="
				+ Integer.toBinaryString(blinkState) + ", 报警标志位=" + Integer.toBinaryString(buzzerState) + "]";
	}

	public int getLightState() {
		return lightState;
	}

	public void setLightState(int lightState) {
		this.lightState = lightState;
	}

	public int getBlinkState() {
		return blinkState;
	}

	public void setBlinkState(int blinkState) {
		this.blinkState = blinkState;
	}

	public int getBuzzerState() {
		return buzzerState;
	}

	public void setBuzzerState(int buzzerState) {
		this.buzzerState = buzzerState;
	}

	@Override
	public Code getCode() {
		return AccessoryCode.FourLightStateCode;
	}

}
