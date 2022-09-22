package com.nltecklib.protocol.li.check2;

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
import com.nltecklib.protocol.li.check2.Check2Environment.CalDot;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Check2WriteCalFlashData extends Data implements Configable, Queryable, Responsable {

	private static final String SPIT = ",";
	private Map<String, List<CalDot>> calDotMap = new HashMap<String, List<CalDot>>();
	private static List<String> modeList = new ArrayList<>();

//	public static final int ADC_BIT_COUNT = 1;
//	public static final int K_BIT_COUNT = 7;
//	public static final int B_BIT_COUNT = 7;

	static {
		modeList.add(VoltMode.Backup + SPIT + Pole.NORMAL);
		modeList.add(VoltMode.Power + SPIT + Pole.NORMAL);
		modeList.add(VoltMode.Backup + SPIT + Pole.REVERSE);
		modeList.add(VoltMode.Power + SPIT + Pole.REVERSE);
	}

	public static String unionKey(VoltMode mode, Pole pole) {
		return mode + SPIT + pole;
	}

	public Check2WriteCalFlashData() {
		// TODO Auto-generated constructor stub
		for (String mode : modeList) {
			calDotMap.put(mode, new ArrayList<>());
		}
	}

	public static List<String> getModeList() {
		return modeList;
	}

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

		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}

		data.add((byte) chnIndex);

		for (String mode : modeList) {

			data.add((byte) calDotMap.get(mode).size());

			for (int i = 0; i < calDotMap.get(mode).size(); i++) {
				CalDot dot = calDotMap.get(mode).get(i);

				data.addAll(Arrays.asList(
						ProtocolUtil.split((long) (dot.adcCalculate * Math.pow(10, voltageResolution)), 3, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.split((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
			}
		}
	}

	private boolean isDotEmpty(CalDot dot) {

		if (dot.adc == 0 && dot.adcK == 0 && dot.adcB == 0) {

			return true;
		}

		return false;
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

		for (String mode : modeList) {
			int count = ProtocolUtil.getUnsignedByte(data.get(index++));
			for (int i = 0; i < count; i++) {
				CalDot dot = new CalDot();

				dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / Math.pow(10, voltageResolution);
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, adcKResolution);
				index += 4;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, adcBResolution);
				index += 4;
				calDotMap.get(mode).add(dot);
			}
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.WriteCalFlashCode;
	}

	public Map<String, List<CalDot>> getCalDotMap() {
		return calDotMap;
	}

	public void setCalDotMap(Map<String, List<CalDot>> calDotMap) {
		this.calDotMap = calDotMap;
	}


	@Override
	public String toString() {
		return "Check2WriteCalFlashData [calDotMap=" + calDotMap + "]";
	}

}
