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
import com.nltecklib.protocol.lab.pickup.PMultiModuleCaculateData;
import com.nltecklib.protocol.lab.pickup.PCalculateExData.AdcReadonly;
import com.nltecklib.protocol.lab.pickup.PMultiModuleCaculateData.ReadonlyAdcData;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年4月13日 下午4:49:38 类说明
 */
public class CalChnMeterExData extends Data implements Configable, Queryable, Responsable {

	private byte moduleIndex;
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

	private List<AdcReadonly> adcVals = new ArrayList<>();
	private List<ReadonlyAdcData> adcDatas = new ArrayList<>();

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		// 极性
		// 工作方式
		if (Data.isUseModuleCal()) {
			data.add((byte) moduleIndex);
		}
		data.add((byte) workMode.ordinal());

		data.add((byte) precisionLevel);
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 1000), 4, true)));
		// errCode
		data.add((byte) errCode.ordinal());
		// 程控K
		data.addAll(Arrays.asList(
				ProtocolUtil.splitSpecialMinus((long) (programK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// 程控B
		data.addAll(Arrays.asList(
				ProtocolUtil.splitSpecialMinus((long) (programB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
		// 程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programVal, 2, true)));
		// adc K
		data.addAll(Arrays
				.asList(ProtocolUtil.splitSpecialMinus((long) (adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
		// adc B
		data.addAll(Arrays
				.asList(ProtocolUtil.splitSpecialMinus((long) (adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

		// data.add((byte) bigType);

		if (!Data.isUseModuleCal()) {
			data.add((byte) adcVals.size());

			for (AdcReadonly adcVal : adcVals) {
				// primitive adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.primitiveAdc * 1000), 4, true)));
				// final Adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.finalAdc * 1000), 4, true)));

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.primitiveBackAdc * 1000), 4, true)));
				// final Adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.finalBackAdc * 1000), 4, true)));

				// 原始功率电压ADC
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.primitivePowerAdc * 1000), 4, true)));
				// 最终功率电压Adc
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcVal.finalPowerAdc * 1000), 4, true)));
			}
		}

		// wyj
		if (Data.isUseModuleCal()) {
			data.add((byte) adcDatas.size());
			for (ReadonlyAdcData a : adcDatas) {
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (a.primitiveBackAdc1 * Math.pow(10, 3)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (a.finalBackAdc1 * Math.pow(10, 3)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (a.primitiveBackAdc2 * Math.pow(10, 3)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (a.finalBackAdc2 * Math.pow(10, 3)), 4, true)));
				data.add((byte) a.adcList.size());
				for (PMultiModuleCaculateData.AdcEntry ae : a.adcList) {
					data.addAll(Arrays.asList(ProtocolUtil.split((long) (ae.primitiveAdc * Math.pow(10, 3)), 4, true)));
					data.addAll(Arrays.asList(ProtocolUtil.split((long) (ae.finalAdc * Math.pow(10, 3)), 4, true)));
				}
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		if (Data.isUseModuleCal()) {
			moduleIndex = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
		}
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalMode.values().length - 1) {

			throw new RuntimeException("error workmode code :" + code);
		}
		workMode = CalMode.values()[code];

		// 双精度

		precisionLevel = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
		index += 4;

		// errCode
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= ErrCode.values().length) {

			throw new RuntimeException("the errCode value is error:" + flag);
		}
		errCode = ErrCode.values()[flag];
		// 程控K
		programK = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// 程控B
		programB = (double) ProtocolUtil
				.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;
		// 程控值
		programVal = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc k
		adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]),
				true) / Math.pow(10, FACTOR_EXP_K);
		index += BIT_COUNT_K;
		// adc b
		adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]),
				true) / Math.pow(10, FACTOR_EXP_B);
		index += BIT_COUNT_B;

		// bigType = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (!Data.isUseModuleCal()) {
			int length = ProtocolUtil.getUnsignedByte(data.get(index++));

			for (int i = 0; i < length; i++) {
				AdcReadonly adcVal = new AdcReadonly();
				// 原始ADC
				adcVal.primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				// final Adc
				adcVal.finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				// 原始备份电压ADC
				adcVal.primitiveBackAdc = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;

				// 最终备份电压Adc
				adcVal.finalBackAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				// 原始功率电压ADC
				adcVal.primitivePowerAdc = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;

				// 最终功率电压Adc
				adcVal.finalPowerAdc = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
				index += 4;

				adcVals.add(adcVal);
			}
		} else {
			// wyj
			// index++; //略过 adcVals 长度
			this.adcDatas = new ArrayList<ReadonlyAdcData>();

			int tmpCount = ProtocolUtil.getUnsignedByte(data.get(index++));
			for (int i = 0; i < tmpCount; i++) {
				ReadonlyAdcData a = new ReadonlyAdcData();
				a.primitiveBackAdc1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / Math.pow(10, 3);
				index += 4;
				a.finalBackAdc1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / Math.pow(10, 3);
				index += 4;
				a.primitiveBackAdc2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / Math.pow(10, 3);
				index += 4;
				a.finalBackAdc2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / Math.pow(10, 3);
				index += 4;

				a.adcList = new ArrayList<PMultiModuleCaculateData.AdcEntry>();
				// adcDatas.get(i).adcList = new ArrayList<PMultiModuleCaculateData.AdcEntry>();
				int tmpModuleCount = ProtocolUtil.getUnsignedByte(data.get(index++));
				for (int j = 0; j < tmpModuleCount; j++) {
					PMultiModuleCaculateData.AdcEntry ae = new PMultiModuleCaculateData.AdcEntry();
					ae.primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
							true) / Math.pow(10, 3);
					index += 4;
					ae.finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
							true) / Math.pow(10, 3);
					index += 4;

					a.adcList.add(ae);
				}

				adcDatas.add(a);

			}
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CalChnMeterExCode;
	}

	public int getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(byte moduleIndex) {
		this.moduleIndex = moduleIndex;
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

	public int getPrecisionLevel() {
		return precisionLevel;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.precisionLevel = precisionLevel;
	}

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
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

	public List<AdcReadonly> getAdcVals() {
		return adcVals;
	}

	public void setAdcVals(List<AdcReadonly> adcVals) {
		this.adcVals = adcVals;
	}

	public List<ReadonlyAdcData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<ReadonlyAdcData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	@Override
	public String toString() {
		return "CalChnMeterExData [workMode=" + workMode + ", calculateDot=" + calculateDot + ", precisionLevel="
				+ precisionLevel + ", errCode=" + errCode + ", programK=" + programK + ", programB=" + programB
				+ ", adcK=" + adcK + ", adcB=" + adcB + ", programVal=" + programVal + ", adcVals=" + adcVals + "]"
				+ ", adcDatas=" + adcDatas + "]";
	}

}
