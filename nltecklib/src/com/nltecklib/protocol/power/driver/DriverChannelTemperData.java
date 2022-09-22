/**
 * 
 */
package com.nltecklib.protocol.power.driver;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 驱动板通道温度查询 0x36
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年7月6日 下午1:07:41
 *
 */
public class DriverChannelTemperData extends Data implements Queryable, Responsable {

	/** 温度探头集合 */
	private List<Double> tempProbes = new ArrayList<>();

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
		// TODO Auto-generated method stub

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		/** 探头数量 */
		int probes = ProtocolUtil.getUnsignedByte(data.get(index++));

		tempProbes.clear();

		for (int i = 0; i < probes; i++) {

			tempProbes.add((double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
		}

	}

	@Override
	public Code getCode() {

		return DriverCode.ChannelTemperCode;
	}

	public List<Double> getTempProbes() {
		return tempProbes;
	}

	public void setTempProbes(List<Double> tempProbes) {
		this.tempProbes = tempProbes;
	}

	@Override
	public String toString() {
		return "DriverChannelTemperData [tempProbes=" + tempProbes + "]";
	}

	

}
