package com.nltecklib.protocol.li.logic;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 逻辑板计量通信协议
 * 
 * @author Administrator
 *
 */
public class LogicCalculateData extends Data implements Queryable, Configable, Responsable {

	private Pole pole = Pole.NORMAL;
	private WorkMode workMode = WorkMode.SLEEP;
	private double calculateDot; // 计量点
	private double finalAdc; // 最终ADC
	private boolean ready;

	// 只读属性
	private double primitiveADC; // 原始ADC
	private double programK;
	private double programB;
	private double adcK;
	private double adcB;
	private long programVal; // 程控值

	public LogicCalculateData() {

	}

	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add(isReverseDriverChnIndex()
				? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: (byte) chnIndex);
		// 极性
		data.add((byte) pole.ordinal());
		// 工作方式
		data.add((byte) workMode.ordinal());
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 100), 3, true)));
		// ready
		data.add((byte) (ready ? 1 : 0));
		// 程控K
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programK * Math.pow(10, 7)), 4, true)));
		// 程控B
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programB * Math.pow(10, 7)), 4, true)));
		// 程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programVal, 2, true)));
		// adc K
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK * Math.pow(10, 7)), 4, true)));
		// adc B
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcB * Math.pow(10, 7)), 4, true)));
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
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			// 驱动板通道反序
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

		}
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
		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

		// ready
		ready = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		// 程控K
		programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 8);
		index += 4;
		// 程控B
		programB = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 8);
		index += 4;
		// 程控值
		programVal = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc k
		adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 8);
		index += 4;
		// adc b
		adcB = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 8);
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

		return LogicCode.CalculateCode;
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

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
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

}
