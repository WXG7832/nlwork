package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.CalculateAdcVal;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalMode;
import com.nltecklib.protocol.lab.main.MainEnvironment.ErrCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 通道计量
 * 
 * @author Administrator
 *
 */
public class CalChnMeterData extends Data implements Configable, Queryable, Responsable {

	private CalMode workMode = CalMode.SLEEP;
	private double calculateDot; // 计量点
	private int precisionLevel; // 精度等级
	private ErrCode errCode = ErrCode.NORMAL; // 故障码

	// 只读属性
	private double programK;
	private double programB;
	private double adcK;
	private double adcB;
	private long programVal; // 程控值

	private List<CalculateAdcVal> adcVals = new ArrayList<CalculateAdcVal>();

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	public CalMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalMode workMode) {
		this.workMode = workMode;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}

	public List<CalculateAdcVal> getAdcVals() {
		return adcVals;
	}

	public void setAdcVals(List<CalculateAdcVal> adcVals) {
		this.adcVals = adcVals;
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

	public long getProgramVal() {
		return programVal;
	}

	public void setProgramVal(long programVal) {
		this.programVal = programVal;
	}

//	public static int getFactorExp() {
//		return FACTOR_EXP;
//	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		// 极性
		// 工作方式
		data.add((byte) workMode.ordinal());

		data.add((byte) precisionLevel);
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 1000), 3, true)));
		// errCode
		data.add((byte) errCode.ordinal());
		// 程控K
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (programK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// 程控B
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (programB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
		// 程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programVal, 2, true)));
		// adc K
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// adc B
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

		data.add((byte) adcVals.size());

		for (CalculateAdcVal adcVal : adcVals) {
			// primitive adc
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.primitiveAdc * 1000), 3, true)));
			// final Adc
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.finalAdc * 1000), 3, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalMode.values().length - 1) {

			throw new RuntimeException("error workmode code :" + code);
		}
		workMode = CalMode.values()[code];

		// 双精度

		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
		index += 3;

		// errCode
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= ErrCode.values().length) {

			throw new RuntimeException("the errCode value is error:" + flag);
		}
		errCode = ErrCode.values()[flag];
		// 程控K
		programK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// 程控B
		programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;
		// 程控值
		programVal = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc k
		adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// adc b
		adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int i = 0; i < length; i++) {
			CalculateAdcVal adcVal = new CalculateAdcVal();
			// 原始ADC
			adcVal.primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 1000;
			index += 3;

			// final Adc
			adcVal.finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;

			adcVals.add(adcVal);
		}

	}

	@Override
	public Code getCode() {
		return MainCode.CalChnMeterCode;
	}

	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

	@Override
	public String toString() {
		return "CalChnMeterData [workMode=" + workMode + ", calculateDot=" + calculateDot + ", precisionLevel="
				+ precisionLevel + ", errCode=" + errCode + ", programK=" + programK + ", programB=" + programB
				+ ", adcK=" + adcK + ", adcB=" + adcB + ", programVal=" + programVal + ", adcVals=" + adcVals + "]";
	}
	

}
