package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.CalculateAdcVal;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年2月17日 下午3:26:54 类说明
 */
public class PCalculateExData extends Data implements Configable, Queryable, Responsable {

	private CalWorkMode workMode;
	private double calculateValue; // 实际计量点
	private double programK;
	private double programB;
	private int programVal;
	private double adcK;
	private double adcB;

	private double backAdcK;
	private double backAdcB;

	private double powerAdcK;
	private double powerAdcB;

	private List<AdcReadonly> adcDatas = new ArrayList<>();

	public static class AdcReadonly {

		public double primitiveAdc;
		public double finalAdc;

		public double primitiveBackAdc;
		public double finalBackAdc;

		public double primitivePowerAdc;
		public double finalPowerAdc;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) workMode.ordinal());
		// 计算点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateValue * 1000), 4, true)));

		// 程控K
		data.addAll(Arrays.asList(
				ProtocolUtil.splitSpecialMinus((long) (programK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// 程控B
		data.addAll(Arrays.asList(
				ProtocolUtil.splitSpecialMinus((long) (programB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
		// 程控
		data.addAll(Arrays.asList(ProtocolUtil.split((long) programVal, 2, true)));
		// ADC k
		data.addAll(Arrays
				.asList(ProtocolUtil.splitSpecialMinus((long) (adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// ADC b
		data.addAll(Arrays
				.asList(ProtocolUtil.splitSpecialMinus((long) (adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

		if (workMode == CalWorkMode.CV) {
			// back ADC k
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (backAdcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
			// back ADC b
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (backAdcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

			// back ADC k
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (powerAdcK * Math.pow(10, FACTOR_EXP_K)),
					BIT_COUNT_K, true)));
			// back ADC b
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (powerAdcB * Math.pow(10, FACTOR_EXP_B)),
					BIT_COUNT_B, true)));
		}

		data.add((byte) adcDatas.size());

		for (AdcReadonly val : adcDatas) {
			// 原始ADC
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.primitiveAdc * 1000), 4, true)));
			// final Adc
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.finalAdc * 1000), 4, true)));

			if (workMode == CalWorkMode.CV) {
				// 原始ADC
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.primitiveBackAdc * 1000), 4, true)));
				// final Adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.finalBackAdc * 1000), 4, true)));

				// 原始ADC
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.primitivePowerAdc * 1000), 4, true)));
				// final Adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (val.finalPowerAdc * 1000), 4, true)));
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		// 工作方式
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalWorkMode.values().length - 1) {

			throw new RuntimeException("error work mode code :" + code);
		}
		workMode = CalWorkMode.values()[code];

		// 计算点
		calculateValue = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ 1000;
		index += 4;

		// 程控K
		programK = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// 程控K
		programB = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;
		// 程控值
		programVal = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc K
		adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]),
				true) / Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// adc B
		adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]),
				true) / Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;

		if (workMode == CalWorkMode.CV) {

			// adc K
			backAdcK = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_K);
			index += BIT_COUNT_K;
			// adc B
			backAdcB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_B);
			index += BIT_COUNT_B;

			// adc K
			powerAdcK = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_K);
			index += BIT_COUNT_K;
			// adc B
			powerAdcB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
					/ Math.pow(10, FACTOR_EXP_B);

			index += BIT_COUNT_B;
		}

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int i = 0; i < length; i++) {
			// 原始ADC
			AdcReadonly val = new AdcReadonly();

			val.primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;
			// final adc
			val.finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ 1000;
			index += 4;

			if (workMode == CalWorkMode.CV) {

				val.primitiveBackAdc = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;
				// final adc
				val.finalBackAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				val.primitivePowerAdc = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;
				// final adc
				val.finalPowerAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;
			}

			adcDatas.add(val);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.CalculateExCode;
	}

	public CalWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalWorkMode workMode) {
		this.workMode = workMode;
	}

	public double getCalculateValue() {
		return calculateValue;
	}

	public void setCalculateValue(double calculateValue) {
		this.calculateValue = calculateValue;
	}

	public double getProgramK() {
		return programK;
	}

	public void setProgramK(double programK) {
		this.programK = programK;
	}

	public double getProgramB() {
		return programB;
	}

	public void setProgramB(double programB) {
		this.programB = programB;
	}

	public int getProgramVal() {
		return programVal;
	}

	public void setProgramVal(int programVal) {
		this.programVal = programVal;
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

	public double getBackAdcK() {
		return backAdcK;
	}

	public void setBackAdcK(double backAdcK) {
		this.backAdcK = backAdcK;
	}

	public double getBackAdcB() {
		return backAdcB;
	}

	public void setBackAdcB(double backAdcB) {
		this.backAdcB = backAdcB;
	}

	public double getPowerAdcK() {
		return powerAdcK;
	}

	public void setPowerAdcK(double powerAdcK) {
		this.powerAdcK = powerAdcK;
	}

	public double getPowerAdcB() {
		return powerAdcB;
	}

	public void setPowerAdcB(double powerAdcB) {
		this.powerAdcB = powerAdcB;
	}

	public List<AdcReadonly> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<AdcReadonly> adcDatas) {
		this.adcDatas = adcDatas;
	}

	@Override
	public String toString() {
		return "PCalculateExData [workMode=" + workMode + ", calculateValue=" + calculateValue + ", programK="
				+ programK + ", programB=" + programB + ", programVal=" + programVal + ", adcK=" + adcK + ", adcB="
				+ adcB + ", backAdcK=" + backAdcK + ", backAdcB=" + backAdcB + ", powerAdcK=" + powerAdcK
				+ ", powerAdcB=" + powerAdcB + ", adcDatas=" + adcDatas + "]";
	}

}
