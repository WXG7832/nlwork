package com.nltecklib.protocol.li.MBWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月31日 下午2:26:45 逻辑板flash校准系数写入和读取
 */
public class MBLogicFlashWriteData extends Data implements Configable, Queryable, Responsable {

	// 校准点集合
	private List<CalDot> dots = new ArrayList<>();
	private static List<CalMode> modeList = new ArrayList<>();

	static {
		modeList.add(CalMode.CC);
		modeList.add(CalMode.CV);
		modeList.add(CalMode.DC);
		modeList.add(CalMode.CV2);
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

	private int getDotCountByMode(CalMode mode) {

		int count = 0;
		for (CalDot dot : dots) {

			if (dot.mode == mode) {

				count++;
			}

		}
		return count;

	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) chnIndex);

		// 总校准点数
		data.add((byte) dots.size());
		// 各模式校准点数统计

//		Map<CalMode, List<CalDot>> dotMap = new HashMap<>();
//		dotMap.put(CalMode.CC, dots.stream().filter(x -> x.mode == CalMode.CC).collect(Collectors.toList()));
//		dotMap.put(CalMode.CV, dots.stream().filter(x -> x.mode == CalMode.CV).collect(Collectors.toList()));
//		dotMap.put(CalMode.DC, dots.stream().filter(x -> x.mode == CalMode.DC).collect(Collectors.toList()));
//		dotMap.put(CalMode.CV2, dots.stream().filter(x -> x.mode == CalMode.CV2).collect(Collectors.toList()));
//
//		data.add((byte) dotMap.get(CalMode.CC).size());
//		data.add((byte) dotMap.get(CalMode.CV).size());
//		data.add((byte) dotMap.get(CalMode.DC).size());
//		data.add((byte) dotMap.get(CalMode.CV2).size());
//
//		for (CalMode mode : CalMode.values()) {
//			
//			if(!dotMap.containsKey(mode)) {
//				continue;
//			}
//			for (CalDot dot : dotMap.get(mode)) {
//				
//				data.add((byte) dot.mode.ordinal()); // 模式
//				data.add((byte) dot.pole.ordinal());// 极性
//				data.add((byte) dot.level); // level
//				data.addAll(Arrays.asList(
//						ProtocolUtil.split((long) (dot.adcCalculate * Math.pow(10, currentResolution)), 3, true)));
//				data.addAll(
//						Arrays.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
//				data.addAll(Arrays.asList(
//						ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
//				data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
//				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
//				data.addAll(Arrays
//						.asList(ProtocolUtil.split((long) (dot.programK * Math.pow(10, programKResolution)), 4, true)));
//				data.addAll(Arrays.asList(ProtocolUtil
//						.splitSpecialMinus((long) (dot.programB * Math.pow(10, programBResolution)), 4, true)));
//
//			}
//		}

		for (CalMode mode : modeList) {
			data.add((byte) dots.stream().filter(x -> x.mode == mode).count());
		}

		for (CalMode mode : modeList) {
			for (CalDot dot : dots.stream().filter(x -> x.mode == mode).collect(Collectors.toList())) {

				data.add((byte) dot.mode.ordinal()); // 模式
				data.add((byte) dot.pole.ordinal());// 极性
				data.add((byte) dot.level); // level
				data.addAll(Arrays
						.asList(ProtocolUtil.split((long) (dot.adcCalculate * Math.pow(10, currentResolution)), 3, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
				data.addAll(Arrays
						.asList(ProtocolUtil.split((long) (dot.programK * Math.pow(10, programKResolution)), 4, true)));
				data.addAll(Arrays.asList(
						ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, programBResolution)), 4, true)));

			}
		}

//		data.add((byte) getDotCountByMode(CalMode.CC));
//		data.add((byte) getDotCountByMode(CalMode.CV));
//		data.add((byte) getDotCountByMode(CalMode.DC));
//		data.add((byte) getDotCountByMode(CalMode.CV2));

//		Collections.sort(dots); // 排序
//		for (CalDot dot : dots) {
//
//			data.add((byte) dot.mode.ordinal()); // 模式
//			data.add((byte) dot.pole.ordinal());// 极性
//			data.add((byte) dot.level); // level
//			data.addAll(Arrays
//					.asList(ProtocolUtil.split((long) (dot.adcCalculate * Math.pow(10, currentResolution)), 3, true)));
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
//			data.addAll(Arrays
//					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
//			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
//			data.addAll(Arrays
//					.asList(ProtocolUtil.split((long) (dot.programK * Math.pow(10, programKResolution)), 4, true)));
//			data.addAll(Arrays.asList(
//					ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, programBResolution)), 4, true)));
//
//		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道号
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int dotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		index += 4; // 不需要解析模式点数

		for (int n = 0; n < dotCount; n++) {

			CalDot dot = new CalDot();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("invalid mode index :" + code);
			}

			dot.mode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("invalid pole index :" + code);
			}
			dot.pole = Pole.values()[code];

			// level
			dot.level = ProtocolUtil.getUnsignedByte(data.get(index++));

			dot.adcCalculate = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, currentResolution);
			index += 3;

			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;
			dot.da = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, programKResolution);
			index += 4;
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, programBResolution);
			index += 4;
			dots.add(dot);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MBWorkformCode.LogicFlashWriteCode;
	}

	public List<CalDot> getDots() {
		return dots;
	}

	public void setDots(List<CalDot> dots) {
		this.dots = dots;
	}

	@Override
	public String toString() {
		return "MBLogicFlashWriteData [dots=" + dots + "]";
	}

}
