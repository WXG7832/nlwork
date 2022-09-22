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
 * 0x29÷∏ æµ∆≈‰÷√
 * 
 * @author caichao_tang
 *
 */
public class IndicatorControlData extends Data implements Configable, Queryable, Responsable {

	private byte colorFlag;
	private short lightFlag;

	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;// ∞Âµÿ÷∑
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) driverIndex);
		data.add(colorFlag);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (lightFlag), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		colorFlag = data.get(index++);
		lightFlag = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	}

	@Override
	public Code getCode() {
		return AccessoryCode.IndicatorCode;
	}

	public byte getColorFlag() {
		return colorFlag;
	}

	public void setColorFlag(byte colorFlag) {
		this.colorFlag = colorFlag;
	}

	public short getLightFlag() {
		return lightFlag;
	}

	public void setLightFlag(short lightFlag) {
		this.lightFlag = lightFlag;
	}

}
