package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.LoadMode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电流电压采集——0x15
 * 
 * @author caichao_tang
 *
 */
public class VolAndCurPickUpData extends Data implements Responsable, Alertable {
	private Date packageDate = new Date();
	private LoadMode loadMode = LoadMode.CC;
	private int current;
	private int voltage;
	private int power;
	private ArrayList<Double> chnVolList = new ArrayList<Double>();// 0.1mV

	public Date getPackageDate() {
		return packageDate;
	}

	public void setPackageDate(Date packageDate) {
		this.packageDate = packageDate;
	}

	public LoadMode getLoadMode() {
		return loadMode;
	}

	public void setLoadMode(LoadMode loadMode) {
		this.loadMode = loadMode;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public ArrayList<Double> getChnVolList() {
		return chnVolList;
	}

	public void setChnVolList(ArrayList<Double> chnVolList) {
		this.chnVolList = chnVolList;
	}

	@Override
	public void encode() {
		data.clear();
		data.addAll(Arrays.asList(ProtocolUtil.split(packageDate.getTime(), 8, true)));
		data.add((byte) loadMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split(voltage, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(current, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(power, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(chnVolList.size(), 2, true)));

		for (Double volDouble : chnVolList) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (volDouble * 10), 2, true)));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;

		packageDate.setTime(ProtocolUtil.compose(data.subList(index, index += 8).toArray(new Byte[0]), true));
		loadMode = LoadMode.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
		voltage = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
		current = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
		power = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
		int count = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);

		chnVolList.clear();
		for (int i = 0; i < count; i++) {
			double volData = ProtocolUtil.composeSpecialMinus(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
			chnVolList.add(volData);
		}
	}

	@Override
	public Code getCode() {
		return MainCode.VOL_CUR_PICKUP_CODE;
	}

	@Override
	public String toString() {
		return "VolAndCurPickUpData [packageDate=" + packageDate + ", loadMode=" + loadMode + ", current=" + current
				+ ", voltage=" + voltage + ", power=" + power + ", chnVolList=" + chnVolList + "]";
	}

}
