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
import com.nltecklib.protocol.power.check.ChnnCalData.ADCData;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 通道计量模式功能码0x06（支持配置和查询）
 * @version: v1.0.0
 * @date: 2021年12月29日 上午10:16:43
 *
 */
public class ChnnVarData extends Data implements Configable, Queryable, Responsable {

	private int pickCount;
	private Double adcK;
	private Double adcB;

	private List<FinalADC> adcDatas = new ArrayList<>();

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

		if(adcK != null) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK * Math.pow(10, 6)), 4, true)));
		}
		if(adcB != null) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcB * Math.pow(10, 6)), 4, true)));
		}
		
		for (FinalADC dot : adcDatas) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adc * Math.pow(10, 1)), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.finalADC * Math.pow(10, 1)), 2, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		pickCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 6);
		index += 4;

		adcB = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 6);
		index += 4;

		adcDatas.clear();

		for (int n = 0; n < pickCount; n++) {

			FinalADC finalADC = new FinalADC();

			finalADC.setAdc(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			finalADC.setFinalADC(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			adcDatas.add(finalADC);
		}

	}

	@Override
	public Code getCode() {
		return CheckCode.ChnnVarCode;
	}

	public int getPickCount() {
		return pickCount;
	}

	public void setPickCount(int pickCount) {
		this.pickCount = pickCount;
	}

	public Double getAdcK() {
		return adcK;
	}

	public void setAdcK(Double adcK) {
		this.adcK = adcK;
	}

	public Double getAdcB() {
		return adcB;
	}

	public void setAdcB(Double adcB) {
		this.adcB = adcB;
	}

	public List<FinalADC> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<FinalADC> adcDatas) {
		this.adcDatas = adcDatas;
	}

	public static class FinalADC {
		private Double adc;
		private Double finalADC;

		public double getAdc() {
			return adc;
		}

		public void setAdc(double adc) {
			this.adc = adc;
		}

		public double getFinalADC() {
			return finalADC;
		}

		public void setFinalADC(double finalADC) {
			this.finalADC = finalADC;
		}

		public FinalADC() {
		}

		public FinalADC(double adc, double finalADC) {
			this.adc = adc;
			this.finalADC = finalADC;
		}

	}
}
