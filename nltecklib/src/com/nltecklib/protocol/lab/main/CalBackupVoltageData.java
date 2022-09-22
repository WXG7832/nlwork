package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalBackupMode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 备份电压校准
 * 
 * @author Administrator
 *
 */
public class CalBackupVoltageData extends Data implements Configable, Queryable, Responsable {

	private List<Double> adcs = new ArrayList<Double>(); // 单位0.001mA或mV
	private CalBackupMode mode = CalBackupMode.SLEEP;

	public List<Double> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<Double> adcs) {
		this.adcs = adcs;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		// 模式
		data.add((byte) mode.ordinal());

		data.add((byte) adcs.size());

		for (double adc : adcs) {

			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 1000), 3, true)));
		}
		// ADC
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		// 模式
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalBackupMode.values().length - 1) {

			throw new RuntimeException("error cal backup mode : " + code);
		}
		mode = CalBackupMode.values()[code];
		// ready
		// adc

		int length = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int i = 0; i < length; i++) {

			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
			index += 3;
			adcs.add(adc);
		}
	}

	@Override
	public Code getCode() {
		return MainCode.CalBackupVoltageCode;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	public CalBackupMode getMode() {
		return mode;
	}

	public void setMode(CalBackupMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "CalBackupVoltageData [adcs=" + adcs + ", mode=" + mode + "]";
	}

	
	
}
