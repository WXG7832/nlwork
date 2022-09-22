package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalculateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 逻辑板计量通信协议
 * 
 * @author Administrator
 *
 */
public class Logic2CalculateData extends Data implements Queryable, Configable, Responsable {

	private Pole pole = Pole.NORMAL;
	private CalMode workMode;
	private double calculateDot; // 计量点

	private List<CalculateAdcGroup> groups = new ArrayList<>();

	private double programK;
	private double programB;
	private double adcK2;
	private double adcB2;
	private double adcK1;
	private double adcB1;
	private long programVal; // 程控值

	public Logic2CalculateData() {

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

		// 通道号
		data.add((byte) (isReverseDriverChnIndex()
				? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: chnIndex));
		// 极性
		data.add((byte) pole.ordinal());
		// 工作方式
		data.add((byte) (workMode.ordinal()));
		// 计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * 100), 3, true)));

		// 程控K
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programK * Math.pow(10, programKResolution)), 4, true)));
		// 程控B
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (programB * Math.pow(10, programBResolution)), 4, true)));
		// 程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programVal, 2, true)));
		// adc K1
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK1 * Math.pow(10, adcKResolution)), 4, true)));
		// adc B1
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcB1 * Math.pow(10, adcBResolution)), 4, true)));
		// adc K2
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcK2 * Math.pow(10, adcKResolution)), 4, true)));
		// adc B2
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (adcB2 * Math.pow(10, adcBResolution)), 4, true)));

		data.add((byte) groups.size());

		// for(int n = 0 ; n < groups.size() ; n++) {
		//
		// CalculateAdcGroup group = groups.get(n);
		// // primitive adc
		// data.addAll(Arrays.asList(ProtocolUtil.split((long) (group.primitiveAdc *
		// 100), 3, true)));
		// // final Adc
		// data.addAll(Arrays.asList(ProtocolUtil.split((long) (group.finalAdc * 100),
		// 3, true)));
		// }
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
		if (code > CalMode.values().length - 1) {

			throw new RuntimeException("error workmode code :" + code);
		}
		workMode = CalMode.values()[code]; 
		// 计量点
		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;

		// 程控K
		programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, programKResolution);
		index += 4;
		// 程控B
		programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, programBResolution);
		index += 4;
		// 程控值
		programVal = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// adc k
		adcK1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, adcKResolution);
		index += 4;
		// adc b
		adcB1 = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, adcBResolution);
		index += 4;
		// adc k
		adcK2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, adcKResolution);
		index += 4;
		// adc b
		adcB2 = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10,adcBResolution);
		index += 4;

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int n = 0; n < count; n++) {
			// 原始ADC
			double primitiveADC = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			// 原始ADC
			double ADC1 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 100;
			index += 3;

			// final Adc
			double finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ 100;
			index += 3;

			CalculateAdcGroup group = new CalculateAdcGroup();
			group.adc2 = primitiveADC;
			group.adc1 = ADC1;
			group.finalAdc = finalAdc;
			groups.add(group);
		}

	}

	@Override
	public Code getCode() {

		return Logic2Code.CalculateCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
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

	public double getProgramK() {
		return programK;
	}

	public double getProgramB() {
		return programB;
	}

	

	public double getAdcK2() {
		return adcK2;
	}

	public void setAdcK2(double adcK2) {
		this.adcK2 = adcK2;
	}

	public double getAdcB2() {
		return adcB2;
	}

	public void setAdcB2(double adcB2) {
		this.adcB2 = adcB2;
	}

	public double getAdcK1() {
		return adcK1;
	}

	public void setAdcK1(double adcK1) {
		this.adcK1 = adcK1;
	}

	public double getAdcB1() {
		return adcB1;
	}

	public void setAdcB1(double adcB1) {
		this.adcB1 = adcB1;
	}

	public long getProgramVal() {
		return programVal;
	}

	public List<CalculateAdcGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<CalculateAdcGroup> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "Logic2CalculateData [pole=" + pole + ", workMode=" + workMode + ", calculateDot=" + calculateDot
				+ ", groups=" + groups + ", programK=" + programK + ", programB=" + programB + ", adcK2=" + adcK2
				+ ", adcB2=" + adcB2 + ", adcK1=" + adcK1 + ", adcB1=" + adcB1 + ", programVal=" + programVal + "]";
	}
	
	

}
