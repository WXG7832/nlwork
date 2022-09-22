package com.nltecklib.protocol.li.workform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.HKCalDot;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * @author wavy_zheng
 * @version 创建时间：2020年4月23日 上午10:16:57 类说明
 */
public class CalHKFlashWriteData extends Data implements Configable, Queryable, Responsable {

	public final static int MODE_DOT_COUNT = 30;
	public final static int MODE_DOT_LEFT_BYTES = 0;

	private static final int KB_FACTOR = 7;
	private static final int SUB_KB_FACTOR = 2;

	private List<HKCalDot> ccDots = new ArrayList<HKCalDot>();
	private List<HKCalDot> cvDots = new ArrayList<HKCalDot>();

	private List<HKCalDot> dcDots = new ArrayList<HKCalDot>();
	private List<HKCalDot> dvDots = new ArrayList<HKCalDot>();

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
		data.add((byte) chnIndex);
		// cc30个校准点
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			if (i < ccDots.size()) {
				dot = ccDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}

		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			if (i < cvDots.size()) {
				dot = cvDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}

		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			if (i < dcDots.size()) {
				dot = dcDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}

		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			if (i < dvDots.size()) {
				dot = dvDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays
					.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB1 * Math.pow(10, KB_FACTOR)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programK3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.programB3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = val;

		// cc
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			dot.programK1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;
			dot.programB1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;

			dot.programK2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dot.programK3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			ccDots.add(dot);

			index += MODE_DOT_LEFT_BYTES;
		}

		// CV
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			dot.programK1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;
			dot.programB1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;

			dot.programK2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dot.programK3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			cvDots.add(dot);

			index += MODE_DOT_LEFT_BYTES;
		}

		// dc
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			dot.programK1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;
			dot.programB1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;

			dot.programK2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dot.programK3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dcDots.add(dot);

			index += MODE_DOT_LEFT_BYTES;
		}

		// DV
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			HKCalDot dot = new HKCalDot();
			dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]),
					true) / Math.pow(10, KB_FACTOR);
			index += 4;
			dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
					true) / 100;
			index += 3;

			dot.programK1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;
			dot.programB1 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, KB_FACTOR);
			index += 4;

			dot.programK2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB2 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dot.programK3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;
			dot.programB3 = (double) ProtocolUtil
					.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true)
					/ Math.pow(10, SUB_KB_FACTOR);
			index += 2;

			dvDots.add(dot);

			index += MODE_DOT_LEFT_BYTES;
		}
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return WorkformCode.HKFlashWriteCode;
	}

	public List<HKCalDot> getCcDots() {
		return ccDots;
	}

	public void setCcDots(List<HKCalDot> ccDots) {
		this.ccDots = ccDots;
	}

	public List<HKCalDot> getCvDots() {
		return cvDots;
	}

	public void setCvDots(List<HKCalDot> cvDots) {
		this.cvDots = cvDots;
	}

	public List<HKCalDot> getDcDots() {
		return dcDots;
	}

	public void setDcDots(List<HKCalDot> dcDots) {
		this.dcDots = dcDots;
	}

	public List<HKCalDot> getDvDots() {
		return dvDots;
	}

	public void setDvDots(List<HKCalDot> dvDots) {
		this.dvDots = dvDots;
	}
	
	

}
