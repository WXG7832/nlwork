package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.driver.DriverCalibrateData.AdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月16日 下午5:20:00 类说明
 */
public class MbCalibrateChnData extends Data implements Configable, Queryable, Responsable {

	private int moduleIndex;
	private Pole pole;
	private CalMode mode;
	private int range;
	private int voltageDA;
	private int currentDA;
	private int queryAdcNum;
	private List<AdcData> adcDatas = new ArrayList<>();

	@Override
	public boolean supportDriver() {
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

		data.add((byte) moduleIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) mode.ordinal());
		data.add((byte) range);
		// 程控电压值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) voltageDA, 2, true)));
		// 程控电流值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) currentDA, 2, true)));
		// 下发默认为0
		// data.add((byte) 0);
		data.add((byte) adcDatas.size());

		if (mode == CalMode.CV) {
			for (int n = 0; n < adcDatas.size(); n++) {

				AdcData adcData = adcDatas.get(n);
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcData.mainAdc * 100), 3, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcData.backAdc1 * 100), 3, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcData.backAdc2 * 10), 3, true)));

			}
		} else {

			for (int n = 0; n < adcDatas.size(); n++) {

				AdcData adcData = adcDatas.get(n);
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (adcData.mainAdc * 100) , 3, true)));

			}
		}

	}


	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
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

		range = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 程控电压
		voltageDA = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// 程控电流
		currentDA = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// adc个数
		// int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		queryAdcNum = data.get(index++);

		adcDatas.clear();
		if (mode == CalMode.CV) {
			for (int n = 0; n < queryAdcNum; n++) {

				AdcData adcData = new AdcData();

				adcData.mainAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / 100;
				index += 3;
				adcData.backAdc1 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / 100;
				index += 3;
				adcData.backAdc2 = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / 10;
				index += 3;

				adcDatas.add(adcData);
			}
		} else {

			for (int n = 0; n < queryAdcNum; n++) {

				AdcData adcData = new AdcData();

				adcData.mainAdc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / 100;
				index += 3;
				// adcData.back1OrigADC = null;
				// adcData.back2OrigADC = null;

				adcDatas.add(adcData);

			}
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalBoxDeviceCode.CalibrateChnCode;
	}

	public int getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(int moduleIndex) {
		this.moduleIndex = moduleIndex;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public CalMode getMode() {
		return mode;
	}

	public void setMode(CalMode mode) {
		this.mode = mode;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public List<AdcData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<AdcData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	public int getVoltageDA() {
		return voltageDA;
	}

	public void setVoltageDA(int voltageDA) {
		this.voltageDA = voltageDA;
	}

	public int getCurrentDA() {
		return currentDA;
	}

	public void setCurrentDA(int currentDA) {
		this.currentDA = currentDA;
	}

	public int getQueryAdcNum() {
		return queryAdcNum;
	}

	public void setQueryAdcNum(int queryAdcNum) {
		this.queryAdcNum = queryAdcNum;
	}

	@Override
	public String toString() {
		return "MbCalibrateChnData [moduleIndex=" + moduleIndex + ", pole=" + pole + ", mode=" + mode + ", range="
				+ range + ", voltageDA=" + voltageDA + ", currentDA=" + currentDA + ", queryAdcNum=" + queryAdcNum
				+ ", adcDatas=" + adcDatas + "]";
	}

}
