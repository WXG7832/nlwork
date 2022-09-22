/**
 * 
 */
package com.nltecklib.protocol.power.temper;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 温度调节 0x07(支持配置查询)
 * @author: JenHoard_Shaw
 * @date: 2022年3月25日 下午5:12:18
 *
 */
public class TemperAdjustData extends Data implements Configable, Queryable, Responsable {

	private double temper;

	@Override
	public boolean supportDriver() {

		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (temper * Math.pow(10, 1)), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		temper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;

	}

	@Override
	public Code getCode() {

		return TemperCode.TemperAdjustCode;
	}

	public double getTemper() {
		return temper;
	}

	public void setTemper(double temper) {
		this.temper = temper;
	}

}
