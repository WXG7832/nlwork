package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.power.driver.DriverCalibrateData.AdcData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.LabCalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月2日 上午9:44:52
* 实验室大电流多模块校准协议
*/
public class PMultiModuleCalibrateData extends Data implements Configable, Queryable, Responsable {

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	

	public static class AdcData {

		public double mainAdc;
		public double backAdc1;
		public double backAdc2;
		@Override
		public String toString() {
			return "AdcData [mainAdc=" + mainAdc + ", backAdc1=" + backAdc1 + ", backAdc2=" + backAdc2 + "]";
		}
		
		

	}

	//private byte moduleFlag;
	private int moduleIndex;
	private Pole pole;
	private LabCalMode mode;
	private int range;
	private double programV;
	private double programI;
	private int pickCount;
	private List<AdcData> adcDatas = new ArrayList<>();


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
		data.addAll(Arrays.asList(ProtocolUtil.split((long) programV, 2, true)));
		// 程控电流值
		data.addAll(Arrays.asList(ProtocolUtil.split((long) programI, 2, true)));
		// 下发默认为0
		// data.add((byte) 0);
		data.add((byte) pickCount);
		
		/*for (AdcData dot : adcDatas) {

			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.mainAdc * Math.pow(10, 2)), 3, true)));
			if(mode.equals(CalMode.CV)) {
				data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
						(long) (dot.backAdc1 * Math.pow(10, 2)), 3, true)));
				data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
						(long) (dot.backAdc2 * Math.pow(10, 2)), 3, true)));
			}
		}*/

	}

	
	private int convertIndex(int bit) {
	       
		for (int n = 0; n < 16; n++) {

			if ((0x01 << n & bit) > 0) {

				return n;
			}
		}

		return -1;

	}
	
	

	private int convertIndex(byte bit) {
       
		for (int n = 0; n < 8; n++) {

			if ((0x01 << n & bit) > 0) {

				return n;
			}
		}

		return -1;

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
		if (code > LabCalMode.values().length - 1) {

			throw new RuntimeException("error cal mode code:" + code);
		}
		mode = LabCalMode.values()[code];

		range = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 程控电压
		programV = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// 程控电流
		programI = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// adc个数
		// int count = ProtocolUtil.getUnsignedByte(data.get(index++));

		pickCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		adcDatas.clear();
		if (mode.equals(LabCalMode.CV)) {
			for (int n = 0; n < pickCount; n++) {

				AdcData adcData = new AdcData();

				adcData.mainAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;
				adcData.backAdc1 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;
				adcData.backAdc2 = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				adcDatas.add(adcData);
			}
		} else {

			for (int n = 0; n < pickCount; n++) {

				AdcData adcData = new AdcData();

				adcData.mainAdc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
						true) / 1000;
				index += 4;

				adcDatas.add(adcData);
			}
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.MultiModuleCalibrateCode;
	}

	
	public int getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(int moduleIndex) {
		this.moduleIndex = moduleIndex;
	}

//	public byte getModuleFlag() {
//		return moduleFlag;
//	}
//
//	public void setModuleFlag(byte moduleFlag) {
//		this.moduleFlag = moduleFlag;
//	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public LabCalMode getMode() {
		return mode;
	}

	public void setMode(LabCalMode mode) {
		this.mode = mode;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public double getProgramV() {
		return programV;
	}

	public void setProgramV(double programV) {
		this.programV = programV;
	}

	public double getProgramI() {
		return programI;
	}

	public void setProgramI(double programI) {
		this.programI = programI;
	}

	public List<AdcData> getAdcDatas() {
		return adcDatas;
	}

	public void setAdcDatas(List<AdcData> adcDatas) {
		this.adcDatas = adcDatas;
	}

	public int getPickCount() {
		return pickCount;
	}

	public void setPickCount(int pickCount) {
		this.pickCount = pickCount;
	}

	@Override
	public String toString() {
		return "PMultiModuleCalibrateData [moduleIndex=" + moduleIndex + ", pole=" + pole + ", mode=" + mode
				+ ", range=" + range + ", programV=" + programV + ", programI=" + programI + ", pickCount=" + pickCount
				+ ", adcDatas=" + adcDatas + "]";
	}

	

}
