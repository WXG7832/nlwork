package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.power.driver.DriverCalculateData.AdcEntry;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 下午5:02:53 计量调试协议
 */
public class LogicCalculate2DebugData extends Data implements Configable, Queryable, Responsable {

	private int moduleIndex; // 0表示第1块模片，-1表示选中通道
	private Pole pole;
	private CalMode mode;
	private double calculateDot;// 计量点
	private double programDot; // 原始计量程控下发值
	private double programKReadonly;
	private double programBReadonly;
	private double programDotReadonly;
	private double adcKReadonly;
	private double adcBReadonly;
	private double backAdcKReadonly1;
	private double backAdcBReadonly1;
	private double backAdcKReadonly2;
	private double backAdcBReadonly2;
	private int dataCount;

	private List<ReadonlyAdcData> adcDatas = new ArrayList<>();

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
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
        
		data.add((byte) unitIndex);
		// 通道序号
		data.add((byte) chnIndex);
		
		data.add((byte) moduleIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) mode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (calculateDot * Math.pow(10, 2)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) programDot, 2, true)));
		data.add((byte) adcDatas.size());

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programKReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programBReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programDotReadonly * Math.pow(10, 0)), 2, true)));

		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcKReadonly * Math.pow(10, 7)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcBReadonly * Math.pow(10, 7)), 4, true)));

		if (mode.equals(CalMode.CV)) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcKReadonly1 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcBReadonly1 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcKReadonly2 * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (backAdcBReadonly2 * Math.pow(10, 7)), 4, true)));

		}

		for (int n = 0; n < adcDatas.size(); n++) {

			ReadonlyAdcData dot = adcDatas.get(n);

			for (int m = 0; m < Data.getModuleCount(); m++) {

				AdcEntry entry = dot.adcList.get(m);

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (entry.primitiveAdc * Math.pow(10, 2)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (entry.finalAdc * Math.pow(10, 2)), 4, true)));
				
				//System.out.println(n + " encode entry" + entry);

			}

			if (mode.equals(CalMode.CV)) {
				data.addAll(
						Arrays.asList(ProtocolUtil.split((long) (dot.primitiveBackAdc1 * Math.pow(10, 2)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.finalBackAdc1 * Math.pow(10, 2)), 4, true)));
				data.addAll(
						Arrays.asList(ProtocolUtil.split((long) (dot.primitiveBackAdc2 * Math.pow(10, 1)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.finalBackAdc2 * Math.pow(10, 1)), 4, true)));
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		moduleIndex = data.get(index++);

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole code:" + code);
		}
		pole = Pole.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalMode.values().length - 1) {

			throw new RuntimeException("error cal stepType code:" + code);
		}
		mode = CalMode.values()[code];

		calculateDot = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 100;
		index += 4;
		programDot = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		dataCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		programKReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 7);
		index += 4;
		programBReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 7);
		index += 4;
		programDotReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		adcKReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 7);
		index += 4;
		adcBReadonly = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, 7);
		index += 4;
		
		if (mode.equals(CalMode.CV)) {
			backAdcKReadonly1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 7);
			index += 4;
			backAdcBReadonly1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 7);
			index += 4;
			backAdcKReadonly2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 7);
			index += 4;
			backAdcBReadonly2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, 7);
			index += 4;
		}

		for (int n = 0; n < dataCount; n++) {

			ReadonlyAdcData adcData = new ReadonlyAdcData();

			for (int m = 0; m < Data.getModuleCount(); m++) {

				AdcEntry entry = adcData.adcList.get(m);

				double primitiveAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 100;
				index += 4;

				double finalAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 100;
				index += 4;

				entry.primitiveAdc = primitiveAdc;
				entry.finalAdc = finalAdc;
				
				//System.out.println( n + " decode entry:" + entry);

			}

			if (mode.equals(CalMode.CV)) {

				adcData.primitiveBackAdc1 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 100;
				index += 4;

				adcData.finalBackAdc1 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 100;
				index += 4;

				adcData.primitiveBackAdc2 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 10;
				index += 4;

				adcData.finalBackAdc2 = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 10;
				index += 4;

			}

			adcDatas.add(adcData);
		}
	}

	@Override
	public Code getCode() {
		return PCWorkformCode.LogicCalculate2DebugCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}


	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public int getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(int moduleIndex) {
		this.moduleIndex = moduleIndex;
	}

	public CalMode getMode() {
		return mode;
	}

	public void setMode(CalMode mode) {
		this.mode = mode;
	}

	public double getProgramDot() {
		return programDot;
	}

	public void setProgramDot(double programDot) {
		this.programDot = programDot;
	}

	public double getProgramKReadonly() {
		return programKReadonly;
	}

	public void setProgramKReadonly(double programKReadonly) {
		this.programKReadonly = programKReadonly;
	}

	public double getProgramBReadonly() {
		return programBReadonly;
	}

	public void setProgramBReadonly(double programBReadonly) {
		this.programBReadonly = programBReadonly;
	}

	public double getProgramDotReadonly() {
		return programDotReadonly;
	}

	public void setProgramDotReadonly(double programDotReadonly) {
		this.programDotReadonly = programDotReadonly;
	}

	public double getAdcKReadonly() {
		return adcKReadonly;
	}

	public void setAdcKReadonly(double adcKReadonly) {
		this.adcKReadonly = adcKReadonly;
	}

	public double getAdcBReadonly() {
		return adcBReadonly;
	}

	public void setAdcBReadonly(double adcBReadonly) {
		this.adcBReadonly = adcBReadonly;
	}

	public double getBackAdcKReadonly1() {
		return backAdcKReadonly1;
	}

	public void setBackAdcKReadonly1(double backAdcKReadonly1) {
		this.backAdcKReadonly1 = backAdcKReadonly1;
	}

	public double getBackAdcBReadonly1() {
		return backAdcBReadonly1;
	}

	public void setBackAdcBReadonly1(double backAdcBReadonly1) {
		this.backAdcBReadonly1 = backAdcBReadonly1;
	}

	public double getBackAdcKReadonly2() {
		return backAdcKReadonly2;
	}

	public void setBackAdcKReadonly2(double backAdcKReadonly2) {
		this.backAdcKReadonly2 = backAdcKReadonly2;
	}

	public double getBackAdcBReadonly2() {
		return backAdcBReadonly2;
	}

	public void setBackAdcBReadonly2(double backAdcBReadonly2) {
		this.backAdcBReadonly2 = backAdcBReadonly2;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public List<ReadonlyAdcData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<ReadonlyAdcData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	@Override
	public String toString() {
		return "LogicCalculate2DebugData [moduleIndex=" + moduleIndex + ", pole=" + pole + ", mode=" + mode
				+ ", calculateDot=" + calculateDot + ", programDot=" + programDot + ", programKReadonly="
				+ programKReadonly + ", programBReadonly=" + programBReadonly + ", programDotReadonly="
				+ programDotReadonly + ", adcKReadonly=" + adcKReadonly + ", adcBReadonly=" + adcBReadonly
				+ ", backAdcKReadonly1=" + backAdcKReadonly1 + ", backAdcBReadonly1=" + backAdcBReadonly1
				+ ", backAdcKReadonly2=" + backAdcKReadonly2 + ", backAdcBReadonly2=" + backAdcBReadonly2
				+ ", dataCount=" + dataCount + ", adcDatas=" + adcDatas + "]";
	}

	

}
