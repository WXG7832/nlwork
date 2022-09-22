/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.ArrayList;
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
 * @Description: flash校准参数 功能码 0x07
 * @version: v1.0.0
 * @date: 2021年12月29日 上午10:17:15
 *
 */
public class FlashData extends Data implements Configable, Queryable, Responsable {

	private int calCount;

	private List<CalData> calDatas = new ArrayList<>();

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
		data.add((byte) calCount);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		calCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		calDatas.clear();

		for (int n = 0; n < calCount; n++) {

			CalData calData = new CalData();

			calData.setAdc(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			calData.setAdcK((double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 6));
			index += 4;

			calData.setAdcB((double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 6));
			index += 4;

			calDatas.add(calData);
		}
	}

	@Override
	public Code getCode() {
		return CheckCode.FlashCode;
	}

	public int getCalCount() {
		return calCount;
	}

	public void setCalCount(int calCount) {
		this.calCount = calCount;
	}

	public List<CalData> getCalDatas() {
		return calDatas;
	}

	public void setCalDatas(List<CalData> calDatas) {
		this.calDatas = calDatas;
	}

	public static class CalData {
		private double adc;
		private double adcK;
		private double adcB;

		public double getAdc() {
			return adc;
		}

		public void setAdc(double adc) {
			this.adc = adc;
		}

		public double getAdcK() {
			return adcK;
		}

		public void setAdcK(double adcK) {
			this.adcK = adcK;
		}

		public double getAdcB() {
			return adcB;
		}

		public void setAdcB(double adcB) {
			this.adcB = adcB;
		}

		public CalData() {
		}

		public CalData(double adc, double adcK, double adcB) {
			this.adc = adc;
			this.adcK = adcK;
			this.adcB = adcB;
		}

	}
}
