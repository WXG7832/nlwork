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
import com.nltecklib.protocol.lab.main.MainEnvironment.CalDot;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 写入通道校准数据
 * 
 * @author Administrator
 *
 */
public class CalSaveChnFlashData extends Data implements Configable, Queryable, Responsable {
	public final static int MODE_DOT_COUNT = 30;
	public final static int MODE_DOT_LEFT_BYTES = 4;
	public static final int ADC_BIT_COUNT = 3;

	private static String[] modeArray = new String[] { "CC", "CV", "DC", "DV" };

	private Map<String, List<CalDot>> dotMap;

	public CalSaveChnFlashData() {
		dotMap = new HashMap<String, List<CalDot>>();
		for (int i = 0; i < modeArray.length; i++) {
			dotMap.put(modeArray[i], new ArrayList<CalDot>());
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

			// cc15个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
				if (j < dotMap.get(modeArray[i]).size()) {
					dot = dotMap.get(modeArray[i]).get(j);
				}

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, ADC_BIT_COUNT)), 3, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split(dot.da, 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 1000), 3, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, FACTOR_EXP_B)), BIT_COUNT_B, true)));
				for (int k = 0; k < MODE_DOT_LEFT_BYTES; k++) {
					data.add((byte) 0);
				}
			}
		}
	}

	private boolean isDotEmpty(CalDot dot) {

		if (dot.adc == 0 && dot.meter == 0 && dot.adcK == 0 && dot.adcB == 0 && dot.programB == 0
				&& dot.programK == 0) {

			return true;
		}

		return false;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;

		for (int i = 0; i < modeArray.length; i++) {
			// cc30个校准点
			for (int j = 0; j < MODE_DOT_COUNT; j++) {

				CalDot dot = new CalDot();
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
				dot.da = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
						/ 1000;
				index += 3;
				dot.programK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_K);
				index += BIT_COUNT_K;
				dot.programB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_B);
				index += BIT_COUNT_B;

				if (dot.adc != 0) {

					dotMap.get(modeArray[i]).add(dot);
				}

				index += MODE_DOT_LEFT_BYTES; // 补足8个0
			}
		}
	}

	@Override
	public Code getCode() {
		return MainCode.CalSaveChnFlashCode;
	}

	public void addDot(CalDot dot, int index) throws Exception {
		String key = getDotKey(dot);
		if (dotMap.containsKey(key)) {

			if (dotMap.get(key).size() >= MODE_DOT_COUNT) {
				throw new Exception("每个工作模式最多只能存储" + MODE_DOT_COUNT + "个校准点");
			}
			if (index == -1) {
				dotMap.get(key).add(dot);
			} else {
				dotMap.get(key).add(index, dot);
			}
		}
	}

	public static String[] getModeArray() {
		return modeArray;
	}

	public static void setModeArray(String[] modeArray) {
		CalSaveChnFlashData.modeArray = modeArray;
	}

	public Map<String, List<CalDot>> getDotMap() {
		return dotMap;
	}

	public void setDotMap(Map<String, List<CalDot>> dotMap) {
		this.dotMap = dotMap;
	}

	private String getDotKey(CalDot dot) {
		String key = "";
		key += dot.workMode.toString();
		return key;
	}

	@Override
	public String toString() {
		return "CalSaveChnFlashData [dotMap=" + dotMap + "]";
	}

}
