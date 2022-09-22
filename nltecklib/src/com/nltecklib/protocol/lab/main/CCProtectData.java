package com.nltecklib.protocol.lab.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CCProtectData extends Data implements Configable, Queryable, Responsable, Cloneable {
	/**
	 * 电压连续下限个数,必须和电压连续下降幅度同时配置
	 */
	private int voltDescCount;
	/**
	 * 电压连续下降幅度mV
	 */
	private double voltDescVal;
	/**
	 * 电压上限mV,默认值4.5V
	 */
	private double voltUpper;
	/**
	 * 电压下限mV
	 */
	private double voltLower;
	/**
	 * 电压上升上限
	 */
	private double voltAscValUpper;
	/**
	 * 电压上升下限
	 */
	private double voltAscValLower;
	/**
	 * 电压上升单位时间
	 */
	private int voltAscUnitSeconds;
	/**
	 * 电流偏移量mA
	 */
	private double currOffsetVal;
	/**
	 * 电流偏移百分比%
	 */
	private double currOffsetPercent;
	/**
	 * 电压波动值mV
	 */
	private double voltWaveVal;
	/**
	 * 电压波动百分比%
	 */
	private double voltWavePercent;
	/**
	 * 容量上限值mAh
	 */
	private double capacityUpper;
	/**
	 * 时间上限值min
	 */
	private int minuteUpper;

	@Override
	public void encode() {

		data.add((byte) voltDescCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltDescVal * 10), 2, true)));
		// 电压上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltLower * 10), 2, true)));
		// 电压上升速率
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltAscValUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltAscValLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(voltAscUnitSeconds, 2, true)));
		// 电流超差波动
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (currOffsetVal * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (currOffsetPercent * 10), 2, true)));
		// 电压超差保护
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltWaveVal * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltWavePercent * 10), 2, true)));
		// 容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (capacityUpper), 4, true)));
		// 时间
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		voltDescCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		voltDescVal = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		// 电压上下限
		voltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		// 电压上升速率
		voltAscValUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltAscValLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltAscUnitSeconds = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		// 电流超差波动
		currOffsetVal = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		currOffsetPercent = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
		// 电压超差保护
		voltWaveVal = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltWavePercent = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		// 容量
		capacityUpper = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		// 时间
		minuteUpper = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {

		return MainCode.CCProtectCode;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getVoltDescCount() {
		return voltDescCount;
	}

	public void setVoltDescCount(int voltDescCount) {
		this.voltDescCount = voltDescCount;
	}

	public double getVoltDescVal() {
		return voltDescVal;
	}

	public void setVoltDescVal(double voltDescVal) {
		this.voltDescVal = voltDescVal;
	}

	public double getVoltUpper() {
		return voltUpper;
	}

	public void setVoltUpper(double voltUpper) {
		this.voltUpper = voltUpper;
	}

	public double getVoltLower() {
		return voltLower;
	}

	public void setVoltLower(double voltLower) {
		this.voltLower = voltLower;
	}

	public double getVoltAscValUpper() {
		return voltAscValUpper;
	}

	public void setVoltAscValUpper(double voltAscValUpper) {
		this.voltAscValUpper = voltAscValUpper;
	}

	public double getVoltAscValLower() {
		return voltAscValLower;
	}

	public void setVoltAscValLower(double voltAscValLower) {
		this.voltAscValLower = voltAscValLower;
	}

	public int getVoltAscUnitSeconds() {
		return voltAscUnitSeconds;
	}

	public void setVoltAscUnitSeconds(int voltAscUnitSeconds) {
		this.voltAscUnitSeconds = voltAscUnitSeconds;
	}

	public double getCurrOffsetVal() {
		return currOffsetVal;
	}

	public void setCurrOffsetVal(double currOffsetVal) {
		this.currOffsetVal = currOffsetVal;
	}

	public double getCurrOffsetPercent() {
		return currOffsetPercent;
	}

	public void setCurrOffsetPercent(double currOffsetPercent) {
		this.currOffsetPercent = currOffsetPercent;
	}

	public double getVoltWaveVal() {
		return voltWaveVal;
	}

	public void setVoltWaveVal(double voltWaveVal) {
		this.voltWaveVal = voltWaveVal;
	}

	public double getVoltWavePercent() {
		return voltWavePercent;
	}

	public void setVoltWavePercent(double voltWavePercent) {
		this.voltWavePercent = voltWavePercent;
	}

	public double getCapacityUpper() {
		return capacityUpper;
	}

	public void setCapacityUpper(double capacityUpper) {
		this.capacityUpper = capacityUpper;
	}

	public int getMinuteUpper() {
		return minuteUpper;
	}

	public void setMinuteUpper(int minuteUpper) {
		this.minuteUpper = minuteUpper;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		return "CCProtectData [voltDescCount=" + voltDescCount + ", voltDescVal=" + voltDescVal + ", voltUpper="
				+ voltUpper + ", voltLower=" + voltLower + ", voltAscValUpper=" + voltAscValUpper + ", voltAscValLower="
				+ voltAscValLower + ", voltAscUnitSeconds=" + voltAscUnitSeconds + ", currOffsetVal=" + currOffsetVal
				+ ", currOffsetPercent=" + currOffsetPercent + ", voltWaveVal=" + voltWaveVal + ", voltWavePercent="
				+ voltWavePercent + ", capacityUpper=" + capacityUpper + ", minuteUpper=" + minuteUpper + "]";
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj instanceof CCProtectData) {
			CCProtectData cc = (CCProtectData) obj;
			if (cc.getVoltDescCount() != voltDescCount) {
				return false;
			}
			if (cc.getVoltDescVal() != voltDescVal) {
				return false;
			}
			if (cc.getVoltUpper() != voltUpper) {
				return false;
			}
			if (cc.getVoltLower() != voltLower) {
				return false;
			}
			if (cc.getVoltAscValUpper() != voltAscValUpper) {
				return false;
			}
			if (cc.getVoltAscValLower() != voltAscValLower) {
				return false;
			}
			if (cc.getVoltAscUnitSeconds() != voltAscUnitSeconds) {
				return false;
			}
			if (cc.getCurrOffsetVal() != currOffsetVal) {
				return false;
			}
			if (cc.getCurrOffsetPercent() != currOffsetPercent) {
				return false;
			}
			if (cc.getVoltWaveVal() != voltWaveVal) {
				return false;
			}
			if (cc.getVoltWavePercent() != voltWavePercent) {
				return false;
			}
			if (cc.getCapacityUpper() != capacityUpper) {
				return false;
			}
			if (cc.getMinuteUpper() != minuteUpper) {
				return false;
			}

			return true;
		}

		return false;

	}

}
