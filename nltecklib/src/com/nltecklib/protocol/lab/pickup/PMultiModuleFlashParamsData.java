package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.LabCalMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月2日 上午8:59:03
* 多模片校准系数通信协议
*/
public class PMultiModuleFlashParamsData extends Data implements Configable, Queryable, Responsable {

	public final static int K_BIT = 6;
	public final static int B_BIT = 6;

	public static class CalParamData {

		public LabCalMode calMode;
		public Pole pole;
		public int range; // 量程档位
		public double adc; // adc采样值
		public double adcK;
		public double adcB;
		public int da; // 程控DA值
		public double meter; // 万用表值
		public double programK; // 程控K值
		public double programB; // 程控B值

	}

	private int moduleIndex; // 当前膜片序号
	/**
	 * 分类存储
	 */
	private Map<LabCalMode, List<CalParamData>> saveDataMap = new HashMap<>();
	/**
	 * 备份芯片cv1，cv2全部存储到cv模式下，即CV内部存储点 = cv + cv1 + cv2;
	 */
	private int cvDotCount;
	private int cv1DotCount;
	private int cv2DotCount;
	
	public enum Pole {

		NEGTIVE, POSITIVE;
	}

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		// 膜片序号
		data.add((byte) moduleIndex);
		// 点总数
		data.add((byte) getDotCount());
		// SLP dot count
		data.add((byte) (saveDataMap.get(LabCalMode.SLEEP) == null ? 0 : saveDataMap.get(LabCalMode.SLEEP).size()));
		// cc
		data.add((byte) (saveDataMap.get(LabCalMode.CC) == null ? 0 : saveDataMap.get(LabCalMode.CC).size()));
		// cv
		data.add((byte) (saveDataMap.get(LabCalMode.CV) == null ? 0
				: saveDataMap.get(LabCalMode.CV).size() - cv1DotCount - cv2DotCount));
		// dc
		data.add((byte) (saveDataMap.get(LabCalMode.DC) == null ? 0 : saveDataMap.get(LabCalMode.DC).size()));
		// cv1
		data.add((byte) cv1DotCount);
		// cv2
		data.add((byte) cv2DotCount);

		// 存入slp各个校准点
		List<CalParamData> list = saveDataMap.get(LabCalMode.SLEEP) == null ? new ArrayList<>()
				: saveDataMap.get(LabCalMode.SLEEP);
		encodeSaveDots(list);
		// 存储cc各个校准点
		list = saveDataMap.get(LabCalMode.CC) == null ? new ArrayList<>() : saveDataMap.get(LabCalMode.CC);
		encodeSaveDots(list);
		// 存储cv各个校准点
		list = saveDataMap.get(LabCalMode.CV) == null ? new ArrayList<>() : saveDataMap.get(LabCalMode.CV);
		encodeSaveDots(list.subList(0, list.size() - cv1DotCount - cv2DotCount));

		cvDotCount = list.size() - cv1DotCount - cv2DotCount;
		// 存储dc各个校准点
		list = saveDataMap.get(LabCalMode.DC) == null ? new ArrayList<>() : saveDataMap.get(LabCalMode.DC);
		encodeSaveDots(list);

		// 存储cv1系数
		list = saveDataMap.get(LabCalMode.CV) == null ? new ArrayList<>() : saveDataMap.get(LabCalMode.CV);

		if (cv1DotCount > 0) {
			encodeSaveDots(list.subList(cvDotCount, cvDotCount + cv1DotCount));
		}

		// 存储cv2系数
		if (cv2DotCount > 0) {

			encodeSaveDots(list.subList(cvDotCount + cv1DotCount, cvDotCount + cv1DotCount + cv2DotCount));
		}

	}

	private void encodeSaveDots(List<CalParamData> list) {

		for (CalParamData dot : list) {

			data.add((byte) dot.calMode.ordinal());
			data.add((byte) dot.pole.ordinal());
			data.add((byte) dot.range);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, 3)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, K_BIT)), 4, true)));
			data.addAll(
					Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, B_BIT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * Math.pow(10, 3)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.programK * Math.pow(10, K_BIT)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, B_BIT)), 4, true)));
		}

	}

	public int getDotCount() {

		int totalCount = 0;
		for (Iterator<LabCalMode> it = saveDataMap.keySet().iterator(); it.hasNext();) {

			LabCalMode mode = it.next();

			totalCount += saveDataMap.get(mode).size();

		}

		return totalCount;
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		moduleIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 总点数
		int totalCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// SLP dot count
		int sleepDotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// cc
		int ccDotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// cv
		int cvDotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// dc
		int dcDotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// cv1
		int cv1DotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		// cv2
		int cv2DotCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		List<CalParamData> list = new ArrayList<>();
		for (int n = 0; n < sleepDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.put(LabCalMode.SLEEP, list);

		list = new ArrayList<>();
		for (int n = 0; n < ccDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.put(LabCalMode.CC, list);

		list = new ArrayList<>();
		for (int n = 0; n < cvDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.put(LabCalMode.CV, list);

		list = new ArrayList<>();
		for (int n = 0; n < dcDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.put(LabCalMode.DC, list);

		// cv1
		list = new ArrayList<>();
		for (int n = 0; n < cv1DotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.get(LabCalMode.CV).addAll(list);
		this.cv1DotCount = cv1DotCount;

		// cv2
		list = new ArrayList<>();
		for (int n = 0; n < cv2DotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > LabCalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = LabCalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
			index += 4;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, B_BIT);
			index += 4;

			list.add(dot);

		}
		saveDataMap.get(LabCalMode.CV).addAll(list);
		this.cv2DotCount = cv2DotCount;

	}

	@Override
	public Code getCode() {
		return ChipPickupCode.MultiModuleFlashParamsCode;
	}

	public int getModuleIndex() {
		return moduleIndex;
	}

	public void setModuleIndex(int moduleIndex) {
		this.moduleIndex = moduleIndex;
	}

	public int getCv1DotCount() {
		return cv1DotCount;
	}

	public void setCv1DotCount(int cv1DotCount) {
		this.cv1DotCount = cv1DotCount;
	}

	public int getCv2DotCount() {
		return cv2DotCount;
	}

	public void setCv2DotCount(int cv2DotCount) {
		this.cv2DotCount = cv2DotCount;
	}

	public Map<LabCalMode, List<CalParamData>> getSaveDataMap() {
		return saveDataMap;
	}

	public void saveDatas(LabCalMode mode, List<CalParamData> datas) {

		saveDataMap.put(mode, datas);

	}

	@Override
	public String toString() {
		return "DriverCalParamSaveData [moduleIndex=" + moduleIndex + ", saveDataMap=" + saveDataMap + ", cv1DotCount="
				+ cv1DotCount + ", cv2DotCount=" + cv2DotCount + "]";
	}

}
