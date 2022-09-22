/**
 * 
 */
package com.nltecklib.protocol.li.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.ChargeMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.PolarityMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.PrecisionMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 模片充放电、极性、精度设置 功能码0x02支持配置 支持查询
 * @version: v1.0.0
 * @author Admin
 * @date: 2021年11月12日 上午9:43:11
 *
 */
public class DiapItemConfig extends Data implements Configable, Queryable, Responsable {

	private ChargeMode chargeMode;// 模片充放电
	private PolarityMode polarityMode;// 膜片极性
	private PrecisionMode precisionMode;// 膜片精度
	private long delay;// 指令延迟时间

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
		data.add((byte) chargeMode.ordinal());
		data.add((byte) polarityMode.ordinal());
		data.add((byte) precisionMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code_charge = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_charge > ChargeMode.values().length - 1) {

			throw new RuntimeException("error charge mode code : " + code_charge);
		}
		chargeMode = ChargeMode.values()[code_charge];

		int code_polarity = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_polarity > PolarityMode.values().length - 1) {

			throw new RuntimeException("error polarity mode code : " + code_polarity);
		}
		polarityMode = PolarityMode.values()[code_polarity];

		int code_precision = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_precision > PrecisionMode.values().length - 1) {

			throw new RuntimeException("error precision mode code : " + code_precision);
		}
		precisionMode = PrecisionMode.values()[code_precision];

		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.DiapItemConfig;
	}

	public ChargeMode getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(ChargeMode chargeMode) {
		this.chargeMode = chargeMode;
	}

	public PolarityMode getPolarityMode() {
		return polarityMode;
	}

	public void setPolarityMode(PolarityMode polarityMode) {
		this.polarityMode = polarityMode;
	}

	public PrecisionMode getPrecisionMode() {
		return precisionMode;
	}

	public void setPrecisionMode(PrecisionMode precisionMode) {
		this.precisionMode = precisionMode;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

}
