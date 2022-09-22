package com.nltecklib.protocol.lab.backup;

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
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalDot;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BFlashParamsData extends Data implements Configable, Queryable, Responsable {

	public final static int MODE_DOT_COUNT = 30;
	public final static int MODE_DOT_LEFT_BYTES = 19;

	private static CalWorkMode[] modeArray = new CalWorkMode[] { CalWorkMode.CV };

	private Map<CalWorkMode, List<CalDot>> dotMap;

	public BFlashParamsData() {

		dotMap = new HashMap<CalWorkMode, List<CalDot>>();

		for (int i = 0; i < modeArray.length; i++) {
			dotMap.put(modeArray[i], new ArrayList<CalDot>());
		}
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	public Map<CalWorkMode, List<CalDot>> getDotMap() {
		return dotMap;
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

				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 1000), 3, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, FACTOR_EXP_K)), BIT_COUNT_K, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, FACTOR_EXP_B)),BIT_COUNT_B, true)));
//				data.addAll(Arrays.asList(ProtocolUtil.split(dot.da, 2, true)));
//				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
//				data.addAll(Arrays.asList(
//						ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, FACTOR_EXP)), 4, true)));
//				data.addAll(Arrays.asList(
//						ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, FACTOR_EXP)), 4, true)));
				for (int k = 0; k < MODE_DOT_LEFT_BYTES; k++) {
					data.add((byte) 0);
				}
			}
		}

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
						/ 1000;
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_K).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_K);
				index += BIT_COUNT_K;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + BIT_COUNT_B).toArray(new Byte[0]), true)
						/ Math.pow(10, FACTOR_EXP_B);
				index += BIT_COUNT_B;
//				dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
//				index += 2;
//				dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
//						/ 100;
//				index += 3;
//				dot.programK = (double) ProtocolUtil
//						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
//						/ Math.pow(10, FACTOR_EXP);
//				index += 4;
//				dot.programB = (double) ProtocolUtil
//						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
//						/ Math.pow(10, FACTOR_EXP);
//				index += 4;
				if (dot.adc != 0) {
					dotMap.get(modeArray[i]).add(dot);
				}

				index += MODE_DOT_LEFT_BYTES; // 补足21个0
			}
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.FlashCode;
	}

	public void addDot(CalDot dot, CalWorkMode mode) {
		if (dotMap.containsKey(mode)) {
			dotMap.get(mode).add(dot);
		}
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "FlashParamsData [dotMap=" + dotMap + "]";
	}

}
