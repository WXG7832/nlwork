/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 常规电池保护功能码 0x08
 * @version: v1.0.0
 * @date: 2021年12月29日 上午10:17:51
 *
 */
public class BattProtectData extends Data implements Configable, Queryable, Responsable {

	private double volUpper;

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
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (volUpper * Math.pow(10, 1)), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		volUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
	}

	@Override
	public Code getCode() {
		return CheckCode.BatteryProtecCode;
	}

	public double getVolUpper() {
		return volUpper;
	}

	public void setVolUpper(double volUpper) {
		this.volUpper = volUpper;
	}
}
