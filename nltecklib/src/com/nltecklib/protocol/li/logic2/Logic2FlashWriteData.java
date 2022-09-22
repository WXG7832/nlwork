package com.nltecklib.protocol.li.logic2;

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
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class Logic2FlashWriteData extends Data implements Configable, Queryable, Responsable {

//	public static final int ADC_BIT_COUNT = 2;
//	public static final int K_BIT_COUNT = 7;
//	public static final int B_BIT_COUNT = 7;

	public static class CalDot implements Cloneable{

		public CalMode mode;
		public Pole pole;
		public double meter; // 表值
		public double adc; // 驱动板ADC
		public double adcCalculate; // 逻辑板将驱动板adc * 逻辑板K + b 后的计算值，即ADC1
		public double adcK;
		public double adcB;
		public long da;
		public double programK;
		public double programB;
		public int level; // 精度档位，精度越高数字越大

		@Override
		public CalDot clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (CalDot)super.clone();
		}
		
//		@Override
//		public int compareTo(Object o) {
//
//			CalDot dot = (CalDot) o;
//			if (this.mode != dot.mode) {
//
//				return this.mode.ordinal() - dot.mode.ordinal();
//			} else {
//
//				// 比较极性
//				if (this.pole != dot.pole) {
//
//					return dot.pole.ordinal() - this.pole.ordinal(); // 正极性先排
//				} else {					
//					// 比较计量点
//					return (int) (this.meter - dot.meter);
//				}
//			}
//
//		}

		@Override
		public String toString() {
			return "CalDot [mode=" + mode + ", pole=" + pole + ", meter=" + meter + ", adc=" + adc + ", adcCalculate="
					+ adcCalculate + ", adcK=" + adcK + ", adcB=" + adcB + ", da=" + da + ", programK=" + programK
					+ ", programB=" + programB + ", level=" + level + "]";
		}

	}

	// 校准点集合
	private List<CalDot> dots = new ArrayList<>();

	public Logic2FlashWriteData() {

	}

	public void addCalDot(CalDot dot) {

		dots.add(dot);
	}

	public void clearCalDot() {

		dots.clear();
	}

	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
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
		data.add(isReverseDriverChnIndex()
				? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: (byte) chnIndex);

		// 总校准点数
		data.add((byte) dots.size());
		

		data.add((byte) getDotCountByMode(CalMode.CC));
		data.add((byte) getDotCountByMode(CalMode.CV));
		data.add((byte) getDotCountByMode(CalMode.DC));
		data.add((byte) getDotCountByMode(CalMode.CV2));
		
		for (CalDot dot : dots) {

			data.add((byte) dot.mode.ordinal()); // 模式
			data.add((byte) dot.pole.ordinal());// 极性
			data.add((byte) dot.level); // level
			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (dot.adcCalculate * Math.pow(10, currentResolution)), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.split((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.split((long) (dot.programK * Math.pow(10, programKResolution)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, programBResolution)), 4, true)));

		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道号
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}
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

			dot.adcK = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcKResolution);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, adcBResolution);
			index += 4;
			dot.da = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			dot.programK = (double) ProtocolUtil
					.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
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

		return Logic2Code.WriteCalFlashCode;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	@Override
	public boolean supportChannel() {

		return true;
	}

	public List<CalDot> getDots() {
		return dots;
	}

	public void setDots(List<CalDot> dots) {
		this.dots = dots;
	}

	@Override
	public String toString() {
		return "Logic2FlashWriteData [dots=" + dots + "]";
	}

}
