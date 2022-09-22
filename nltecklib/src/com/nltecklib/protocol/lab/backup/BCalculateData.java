package com.nltecklib.protocol.lab.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.CalculateAdcVal;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalBackupMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BCalculateData extends Data implements Configable, Queryable, Responsable {

	private CalBackupMode workMode = CalBackupMode.SLEEP;
	private double calculateValue; // 实际计量点

	private double adcK;
	private double adcB;

	private List<CalculateAdcVal> adcVals = new ArrayList<CalculateAdcVal>();

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) workMode.ordinal());
		// 计算点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateValue * 1000), 3, true)));
		// adcK
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

		data.add((byte) adcVals.size());

		for (CalculateAdcVal vals : adcVals) {
			// 原始ADC
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (vals.primitiveAdc * 1000), 3, true)));
			// 最终ADC
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (vals.finalAdc * 1000), 3, true)));
		}

	}

	public List<CalculateAdcVal> getAdcVals() {
		return adcVals;
	}

	public void setAdcVals(List<CalculateAdcVal> adcVals) {
		this.adcVals = adcVals;
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		// 工作方式
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalBackupMode.values().length - 1) {

			throw new RuntimeException("error work mode code :" + code);
		}
		workMode = CalBackupMode.values()[code];

		// 计算点
		calculateValue = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
				/ 1000;
		index += 3;

		// adc K
		adcK = (double) ProtocolUtil.compose(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// adc B
		adcB = (double) ProtocolUtil.compose(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int i = 0; i < length; i++) {
			CalculateAdcVal val = new CalculateAdcVal();
			// 原始ADC
			val.primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;
			// final adc
			val.finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 1000;
			index += 3;

			adcVals.add(val);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.CheckChnCode;
	}

	public CalBackupMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalBackupMode workMode) {
		this.workMode = workMode;
	}

	public double getCalculateValue() {
		return calculateValue;
	}

	public void setCalculateValue(double calculateValue) {
		this.calculateValue = calculateValue;
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

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "CalculateData [workMode=" + workMode + ", calculateValue=" + calculateValue + ", adcK=" + adcK
				+ ", adcB=" + adcB + ", adcVals=" + adcVals + "]";
	}

	
}
