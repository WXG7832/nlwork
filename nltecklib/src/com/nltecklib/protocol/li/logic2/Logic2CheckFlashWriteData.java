package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.CalDot;
import com.nltecklib.protocol.li.check2.Check2WriteCalFlashData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 上午10:02:29
 *          将回检板校准系数保存到逻辑板内；用于驱动板/逻辑板更换后可以重新将回检板校准系数写入回检板
 */
public class Logic2CheckFlashWriteData extends Data implements Configable, Queryable, Responsable {

	private List<CalDot> backupPosDots = new ArrayList<>();
	private List<CalDot> powerPosDots = new ArrayList<>();

	private List<CalDot> backupNegDots = new ArrayList<>();
	private List<CalDot> powerNegDots = new ArrayList<>();

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
		data.add(isReverseDriverChnIndex()
				? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: (byte) chnIndex);
		// 备份电压正极性
		data.add((byte) backupPosDots.size());
		for (CalDot dot : backupPosDots) {

			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcCalculate * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}
		// 功率电压正极性
		data.add((byte) powerPosDots.size());
		for (CalDot dot : powerPosDots) {

			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcCalculate * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}

		// 备份电压负极性
		data.add((byte) backupNegDots.size());
		for (CalDot dot : backupNegDots) {

			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcCalculate * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
		}
		// 功率电压负极性
		data.add((byte) powerNegDots.size());
		for (CalDot dot : powerNegDots) {

			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcCalculate * Math.pow(10, voltageResolution)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split(
					(long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(
					(long) (dot.adcB * Math.pow(10,adcBResolution)), 4, true)));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

		}
		// 备份电压正极性
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {

			CalDot dot = new CalDot();

			dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
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

			dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
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

			dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
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

			dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / Math.pow(10, voltageResolution);
			index += 3;
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
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
		// TODO Auto-generated method stub
		return Logic2Code.WriteCheckFlashCode;
	}

	public void appendBackupPosDot(CalDot dot) {

		backupPosDots.add(dot);
	}

	public void appendPowerPosDot(CalDot dot) {

		powerPosDots.add(dot);
	}

	public void appendBackupNegDot(CalDot dot) {

		backupNegDots.add(dot);
	}

	public void appendPowerNegDot(CalDot dot) {

		powerNegDots.add(dot);
	}

	public List<CalDot> getBackupPosDots() {
		return backupPosDots;
	}

	public List<CalDot> getPowerPosDots() {
		return powerPosDots;
	}

	public List<CalDot> getBackupNegDots() {
		return backupNegDots;
	}

	public List<CalDot> getPowerNegDots() {
		return powerNegDots;
	}

	
	
	public void setBackupPosDots(List<CalDot> backupPosDots) {
	    this.backupPosDots = backupPosDots;
	}

	public void setPowerPosDots(List<CalDot> powerPosDots) {
	    this.powerPosDots = powerPosDots;
	}

	public void setBackupNegDots(List<CalDot> backupNegDots) {
	    this.backupNegDots = backupNegDots;
	}

	public void setPowerNegDots(List<CalDot> powerNegDots) {
	    this.powerNegDots = powerNegDots;
	}

	@Override
	public String toString() {
		return "Logic2CheckFlashWriteData [backupPosDots=" + backupPosDots + ", powerPosDots=" + powerPosDots
				+ ", backupNegDots=" + backupNegDots + ", powerNegDots=" + powerNegDots + "]";
	}
	
	

}
