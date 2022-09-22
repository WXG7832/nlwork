package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月16日 下午2:44:25 校准系数保存
 */
public class MbFlashParamData extends Data implements Configable, Queryable, Responsable {

	/*
	 * public static class KB_Dot {
	 * 
	 * public CalMode stepType; public Pole pole; public int range; // 量程档位 public
	 * double adc; // adc采样值 public double adcK; public double adcB; public int da;
	 * // 程控DA值 public double meterVal; // 万用表值 public double daK; // 程控K值 public
	 * double daB; // 程控B值
	 * 
	 * }
	 */

	private int moduleIndex; // 当前膜片序号
	/**
	 * 分类存储
	 */
	private Map<CalMode, List<CalParamData>> kb_dotMap = new HashMap<>();

	/**
	 * 备份芯片cv1，cv2全部存储到cv模式下，即CV内部存储点 = cv + cv1 + cv2;
	 */
	private int cv1DotCount;
	private int cv2DotCount;

	@Override
	public boolean supportDriver() {
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

		// 膜片序号
		data.add((byte) moduleIndex);
		// 点总数
		data.add((byte) getDotCount());
		// SLP dot count
		data.add((byte) (kb_dotMap.get(CalMode.SLEEP) == null ? 0 : kb_dotMap.get(CalMode.SLEEP).size()));
		// cc
		data.add((byte) (kb_dotMap.get(CalMode.CC) == null ? 0 : kb_dotMap.get(CalMode.CC).size()));
		// cv
		data.add((byte) (kb_dotMap.get(CalMode.CV) == null ? 0
				: kb_dotMap.get(CalMode.CV).size() - cv1DotCount - cv2DotCount));
		// dc
		data.add((byte) (kb_dotMap.get(CalMode.DC) == null ? 0 : kb_dotMap.get(CalMode.DC).size()));
		// cv1
		data.add((byte) cv1DotCount);
		// cv2
		data.add((byte) cv2DotCount);

		// 存入slp各个校准点
		List<CalParamData> list = kb_dotMap.get(CalMode.SLEEP) == null ? new ArrayList<>()
				: kb_dotMap.get(CalMode.SLEEP);
		encodeKB_DotLst(list);
		// 存储cc各个校准点
		list = kb_dotMap.get(CalMode.CC) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.CC);
		encodeKB_DotLst(list);
		// 存储cv各个校准点
		list = kb_dotMap.get(CalMode.CV) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.CV);
		encodeKB_DotLst(list.subList(0, list.size() - cv1DotCount - cv2DotCount));
		int cvDotCount = list.size() - cv1DotCount - cv2DotCount;
		// 存储dc各个校准点
		list = kb_dotMap.get(CalMode.DC) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.DC);
		encodeKB_DotLst(list);
		// 存储cv1
		list = kb_dotMap.get(CalMode.CV) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.CV);
		if (cv1DotCount > 0) {
			encodeKB_DotLst(list.subList(cvDotCount, cvDotCount + cv1DotCount));
		}
		if (cv2DotCount > 0) {
			encodeKB_DotLst(list.subList(cvDotCount + cv1DotCount, cvDotCount + cv1DotCount + cv2DotCount));
		}

	}

	private void encodeKB_DotLst(List<CalParamData> list) {

		for (CalParamData dot : list) {
			
			
			data.add((byte) dot.calMode.ordinal());
			data.add((byte) dot.pole.ordinal());
			data.add((byte) dot.range);
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, 2)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, DriverCalParamSaveData.K_BIT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, DriverCalParamSaveData.B_BIT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * Math.pow(10, 2)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.programK * Math.pow(10, DriverCalParamSaveData.K_BIT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, DriverCalParamSaveData.B_BIT)), 4, true)));

		}

	}

	public int getDotCount() {

		int totalCount = 0;
		for (Iterator<CalMode> it = kb_dotMap.keySet().iterator(); it.hasNext();) {

			CalMode stepType = it.next();

			totalCount += kb_dotMap.get(stepType).size();

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
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal stepType code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = data.get(index++);

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meterVal
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// daK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// daB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		kb_dotMap.put(CalMode.SLEEP, list);

		list = new ArrayList<>();
		for (int n = 0; n < ccDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal stepType code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = data.get(index++);

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meterVal
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// daK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// daB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		kb_dotMap.put(CalMode.CC, list);

		list = new ArrayList<>();
		for (int n = 0; n < cvDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal stepType code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = data.get(index++);

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meterVal
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// daK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// daB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		kb_dotMap.put(CalMode.CV, list);

		list = new ArrayList<>();
		for (int n = 0; n < dcDotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal stepType code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = data.get(index++);

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meterVal
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// daK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// daB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		kb_dotMap.put(CalMode.DC, list);

		// cv1
		list = new ArrayList<>();
		for (int n = 0; n < cv1DotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		
		kb_dotMap.get(CalMode.CV).addAll(list);
		this.cv1DotCount = cv1DotCount;

		// cv2
		list = new ArrayList<>();
		for (int n = 0; n < cv2DotCount; n++) {

			CalParamData dot = new CalParamData();

			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("error cal mode code:" + code);
			}
			dot.calMode = CalMode.values()[code];

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("error pole code:" + code);
			}
			dot.pole = Pole.values()[code];

			dot.range = ProtocolUtil.getUnsignedByte(data.get(index++));

			// adc
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// adcK
			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// adcB
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			// DA
			dot.da = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// meter
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;

			// programK
			dot.programK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.K_BIT);
			index += 4;

			// programB
			dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, DriverCalParamSaveData.B_BIT);
			index += 4;

			list.add(dot);

		}
		kb_dotMap.get(CalMode.CV).addAll(list);
		this.cv2DotCount = cv2DotCount;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalBoxDeviceCode.FlashParamCode;
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

	public Map<CalMode, List<CalParamData>> getKb_dotMap() {
		return kb_dotMap;
	}

	public void setKb_dotMap(Map<CalMode, List<CalParamData>> kb_dotMap) {
		this.kb_dotMap = kb_dotMap;
	}

	public void append(CalMode mode, List<CalParamData> datas) {

		kb_dotMap.put(mode, datas);

	}

}
