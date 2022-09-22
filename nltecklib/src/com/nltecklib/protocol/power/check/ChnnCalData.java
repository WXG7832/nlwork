/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.driver.DriverCalibrateData.AdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 通道校准模式功能码0x05
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月29日 上午10:12:22
 *
 */
public class ChnnCalData extends Data implements Configable, Queryable, Responsable {

	private int pickCount;
	private List<ADCData> adcDatas = new ArrayList<>();

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
		data.add((byte) pickCount);
		for (ADCData dot : adcDatas) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adc * Math.pow(10, 1)), 2, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		pickCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		adcDatas.clear();

		for (int n = 0; n < pickCount; n++) {

			ADCData adcData = new ADCData();

			adcData.setAdc(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			adcDatas.add(adcData);
		}
	}

	@Override
	public Code getCode() {
		return CheckCode.ChnnCalCode;
	}

	public int getPickCount() {
		return pickCount;
	}

	public void setPickCount(int pickCount) {
		this.pickCount = pickCount;
	}

	public List<ADCData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<ADCData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	public static class ADCData {

		public Double adc;

		public double getAdc() {
			return adc;
		}

		public void setAdc(double adc) {
			this.adc = adc;
		}

		public ADCData() {
		}

		public ADCData(double adc) {
			this.adc = adc;
		}

	}

}
