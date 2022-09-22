package com.nltecklib.protocol.li.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2WriteCalFlashData;
import com.nltecklib.protocol.li.driver.DriverEnvironment.CalDot;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 回检板校准系数写入
 * @author admin
 */
public class DriverCheckCalFactorData extends Data  implements Configable,Queryable, Responsable{

	private List<CalDot> backupPosDots = new ArrayList<>();
	private List<CalDot> powerPosDots = new ArrayList<>();

	private List<CalDot> backupNegDots = new ArrayList<>();
	private List<CalDot> powerNegDots = new ArrayList<>();

	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

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
		
		data.add((byte) driverIndex);
		data.add(isReverseDriverChnIndex()
				? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: (byte) chnIndex);
		// 备份电压正极性
		data.add((byte) backupPosDots.size());
		for (CalDot dot : backupPosDots) {

			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adc * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}
		// 功率电压正极性
		data.add((byte) powerPosDots.size());
		for (CalDot dot : powerPosDots) {

			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adc * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}

		// 备份电压负极性
		data.add((byte) backupNegDots.size());
		for (CalDot dot : backupNegDots) {

			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adc * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}
		// 功率电压负极性
		data.add((byte) powerNegDots.size());
		for (CalDot dot : powerNegDots) {

			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adc * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

		}
		// 备份电压正极性
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			CalDot dot = new CalDot();

			dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;

			backupPosDots.add(dot);

		}
		// 功率电压正极性
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			CalDot dot = new CalDot();

			dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;

			powerPosDots.add(dot);

		}

		// 备份电压负极性
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			CalDot dot = new CalDot();

			dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;

			backupNegDots.add(dot);

		}
		// 功率电压负极性
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			CalDot dot = new CalDot();

			dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;

			powerNegDots.add(dot);

		}
		
	}

	@Override
	public Code getCode() {
		return DriverCode.CheckCalFactorCode;
	}

	
	public List<CalDot> getBackupPosDots() {
		return backupPosDots;
	}

	public void setBackupPosDots(List<CalDot> backupPosDots) {
		this.backupPosDots = backupPosDots;
	}

	public List<CalDot> getPowerPosDots() {
		return powerPosDots;
	}

	public void setPowerPosDots(List<CalDot> powerPosDots) {
		this.powerPosDots = powerPosDots;
	}

	public List<CalDot> getBackupNegDots() {
		return backupNegDots;
	}

	public void setBackupNegDots(List<CalDot> backupNegDots) {
		this.backupNegDots = backupNegDots;
	}

	public List<CalDot> getPowerNegDots() {
		return powerNegDots;
	}

	public void setPowerNegDots(List<CalDot> powerNegDots) {
		this.powerNegDots = powerNegDots;
	}


}
