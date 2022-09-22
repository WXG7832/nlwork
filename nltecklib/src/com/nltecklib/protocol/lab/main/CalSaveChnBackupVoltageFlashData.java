package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.CalBackUpDot;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 写入通道备份电压数据
 * 
 * @author Administrator
 *
 */
public class CalSaveChnBackupVoltageFlashData extends Data implements Configable, Queryable, Responsable {

	public static final int MODE_CHECK_DOT_COUNT = 30; // 每个模式固定校准点数
	public static final int MODE_CHECK_DOT_BYTES = 32; // 每个校准点固定字节数

	public static final int MODE_LEFT_BYTES = 19;
	public static final int ADC_BIT_COUNT = 3;

	private static String[] modeArray = new String[] { "CV" };

	private Map<String, List<CalBackUpDot>> dotMap;

	public CalSaveChnBackupVoltageFlashData() {
		dotMap = new HashMap<String, List<CalBackUpDot>>();
		for (int i = 0; i < modeArray.length; i++) {
			dotMap.put(modeArray[i], new ArrayList<CalBackUpDot>());
		}
	}

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

		for (int i = 0; i < modeArray.length; i++) {
			// 回检校准点
			for (int j = 0; j < MODE_CHECK_DOT_COUNT; j++) {

				CalBackUpDot dot = new CalBackUpDot();
				if (j < dotMap.get(modeArray[i]).size()) {
					dot = dotMap.get(modeArray[i]).get(j);
				}
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, ADC_BIT_COUNT)), 3, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));

				for (int k = 0; k < MODE_LEFT_BYTES; k++) {
					data.add((byte) 0);
				}
			}
		}

	}

	private boolean isDotEmpty(CalBackUpDot dot) {

		if (dot.adc == 0 && dot.adcK == 0 && dot.adcB == 0) {

			return true;
		}

		return false;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		for (int i = 0; i < modeArray.length; i++) {
			for (int j = 0; j < MODE_CHECK_DOT_COUNT; j++) {

				CalBackUpDot dot = new CalBackUpDot();

				dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
						/ Math.pow(10, ADC_BIT_COUNT);
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_K);
				index += BIT_COUNT_K;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_B);
				index += BIT_COUNT_B;

				if (dot.adc != 0) {
					dotMap.get(modeArray[i]).add(dot);
				}

				index += MODE_LEFT_BYTES;
			}
		}
	}

	@Override
	public Code getCode() {
		return MainCode.CalSaveChnBackupVoltageFlashCode;
	}

	public void addDot(CalBackUpDot dot, int index) throws Exception {
		String key = getDotKey(dot);
		if (dotMap.containsKey(key)) {

			if (dotMap.get(key).size() >= MODE_CHECK_DOT_COUNT) {
				throw new Exception("每个工作模式最多只能存储" + MODE_CHECK_DOT_COUNT + "个校准点");
			}

			if (index == -1) {
				dotMap.get(key).add(dot);
			} else {
				dotMap.get(key).add(index, dot);
			}
		}
	}

	private String getDotKey(CalBackUpDot dot) {
		String key = "";

		switch (dot.workMode) {
		case CV:
			key += "CV";
			break;
		case DV:
			key += "DV";
			break;
		}
		return key;
	}

	public static String[] getModeArray() {
		return modeArray;
	}

	public static void setModeArray(String[] modeArray) {
		CalSaveChnBackupVoltageFlashData.modeArray = modeArray;
	}

	public Map<String, List<CalBackUpDot>> getDotMap() {
		return dotMap;
	}

	public void setDotMap(Map<String, List<CalBackUpDot>> dotMap) {
		this.dotMap = dotMap;
	}

	@Override
	public String toString() {
		return "CalSaveChnBackupVoltageFlashData [dotMap=" + dotMap + "]";
	}

}
