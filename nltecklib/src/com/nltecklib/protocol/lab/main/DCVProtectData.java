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

/**
 * @author wavy_zheng
 * @version 创建时间：2021年6月27日 下午8:02:55 类说明
 */
public class DCVProtectData extends Data implements Configable, Queryable, Responsable ,Cloneable{

	/**
	 * 电压上限mV
	 */
	private double voltUpper;
	/**
	 * 电压下限mV
	 */
	private double voltLower;

	/**
	 * 容量上限值mAh
	 */
	private double capacityUpper;
	/**
	 * 时间上限值min
	 */
	private int minuteUpper;

	/**
	 * 温度上限℃
	 */
	private double chnTempUpper;

	/**
	 * 温度下限℃
	 */
	private double chnTempLower;

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

		// 电压上下限
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (voltLower * 10), 2, true)));
		// 容量
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (capacityUpper), 4, true)));
		// 时间
		data.addAll(Arrays.asList(ProtocolUtil.split(minuteUpper, 2, true)));
		// 温度
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnTempUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnTempLower * 10), 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		// 电压上下限
		voltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		voltLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		// 容量
		capacityUpper = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		// 时间
		minuteUpper = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
        //温度
		chnTempUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chnTempLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.CDVProtectCode;
	}

	@Override
	public String toString() {
		return "DCVProtectData [voltUpper=" + voltUpper + ", voltLower=" + voltLower + ", capacityUpper="
				+ capacityUpper + ", minuteUpper=" + minuteUpper + ", chnTempUpper=" + chnTempUpper + ", chnTempLower="
				+ chnTempLower + "]";
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

	public double getChnTempUpper() {
		return chnTempUpper;
	}

	public void setChnTempUpper(double chnTempUpper) {
		this.chnTempUpper = chnTempUpper;
	}

	public double getChnTempLower() {
		return chnTempLower;
	}

	public void setChnTempLower(double chnTempLower) {
		this.chnTempLower = chnTempLower;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DCVProtectData)) {
			return false;
		}
		DCVProtectData other = (DCVProtectData) obj;
		if (Double.doubleToLongBits(capacityUpper) != Double.doubleToLongBits(other.capacityUpper)) {
			return false;
		}
		if (Double.doubleToLongBits(chnTempLower) != Double.doubleToLongBits(other.chnTempLower)) {
			return false;
		}
		if (Double.doubleToLongBits(chnTempUpper) != Double.doubleToLongBits(other.chnTempUpper)) {
			return false;
		}
		if (minuteUpper != other.minuteUpper) {
			return false;
		}
		if (Double.doubleToLongBits(voltLower) != Double.doubleToLongBits(other.voltLower)) {
			return false;
		}
		if (Double.doubleToLongBits(voltUpper) != Double.doubleToLongBits(other.voltUpper)) {
			return false;
		}
		return true;
	}

	
}
