package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.ErrCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.Pole;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 计量数据
 * 
 * @author Administrator
 *
 */
public class CalculateData extends Data implements Queryable, Configable, Responsable {

	private Pole pole = Pole.NORMAL;
	private WorkMode workMode = WorkMode.SLEEP;
	private double calculateDot; // 计量点
	private double finalAdc; // 最终ADC
	private byte ready;
	private int highPrecision; // 高精度
	private ErrCode errCode = ErrCode.NORMAL; // 故障码

	// 只读属性
	private double primitiveADC; // 原始ADC
	private double programK;
	private double programB;
	private double adcK;
	private double adcB;
	private long programVal; // 程控值
	
	private final static int  FACTOR_EXP = 7;

	@Override
	public boolean supportUnit() {

		return true;
	}

	@Override
	public boolean supportDriver() {

		return true;
	}

	@Override
	public boolean supportChannel() {

		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		// 极性
		data.add((byte) pole.ordinal());
		// 工作方式
		data.add((byte) workMode.ordinal());
		// 双精度支持?
		if (isDoubleResolutionSupport()) {

			data.add((byte)highPrecision);
		}
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 100), 3, true)));
		// ready
		data.add(ready);
		// errCode
		data.add((byte) errCode.ordinal());
		// 程控K
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programK * Math.pow(10, FACTOR_EXP)), 4, true)));
		// 程控B
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programB * Math.pow(10, FACTOR_EXP)), 4, true)));
		// 程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programVal, 2, true)));
		// adc K
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK * Math.pow(10, FACTOR_EXP)), 4, true)));
		// adc B
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcB * Math.pow(10, FACTOR_EXP)), 4, true)));
		// primitive adc
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (primitiveADC * 100), 3, true)));
		// final Adc
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (finalAdc * 100), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code :" + code);
		}
		pole = Pole.values()[code];
		// 工作方式
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > WorkMode.values().length - 1) {

			throw new RuntimeException("error workmode code :" + code);
		}
		workMode = WorkMode.values()[code];

		// 双精度
		if (isDoubleResolutionSupport()) {

			highPrecision = ProtocolUtil.getUnsignedByte(data.get(index++));
		}

		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;
		// ready
		ready = data.get(index++);

		// errCode
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= ErrCode.values().length) {

			throw new RuntimeException("the errCode value is error:" + flag);
		}
		errCode = ErrCode.values()[flag];
		// 程控K
		programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP);
		index += 4;
		// 程控B
		programB = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP);
		index += 4;
		// 程控值
		programVal = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc k
		adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP);
		index += 4;
		// adc b
		adcB = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, FACTOR_EXP);
		index += 4;
		// 原始ADC
		primitiveADC = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

		// final Adc
		finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

	}

	@Override
	public Code getCode() {

		return WorkformCode.MeasureCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public double getFinalAdc() {
		return finalAdc;
	}

	public byte getReady() {
		return ready;
	}

	public void setReady(byte ready) {
		this.ready = ready;
	}

	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}

	public int isHighPrecision() {
		return highPrecision;
	}

	public void setHighPrecision(int highPrecision) {
		this.highPrecision = highPrecision;
	}

	public double getPrimitiveADC() {
		return primitiveADC;
	}

	public double getProgramK() {
		return programK;
	}

	public double getProgramB() {
		return programB;
	}

	public double getAdcK() {
		return adcK;
	}

	public double getAdcB() {
		return adcB;
	}

	public long getProgramVal() {
		return programVal;
	}

	public void setFinalAdc(double finalAdc) {
		this.finalAdc = finalAdc;
	}

	public void setPrimitiveADC(double primitiveADC) {
		this.primitiveADC = primitiveADC;
	}

	public void setProgramK(double programK) {
		this.programK = programK;
	}

	public void setProgramB(double programB) {
		this.programB = programB;
	}

	public void setAdcK(double adcK) {
		this.adcK = adcK;
	}

	public void setAdcB(double adcB) {
		this.adcB = adcB;
	}

	public void setProgramVal(long programVal) {
		this.programVal = programVal;
	}

}
