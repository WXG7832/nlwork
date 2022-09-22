package com.nltecklib.protocol.li.PCWorkform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 下午1:53:39 类说明
 */
public class CalibratePlanData extends Data implements Configable, Queryable, Responsable {

	private boolean needValidate; // 需要验证校准结果
	private boolean needCalculateAfterCalibrate;
	private long maxProgramV;
	private long maxProgramI;

	private List<CalibratePlanMode> modes = new ArrayList<>();

	public boolean isNeedValidate() {
		return needValidate;
	}

	public void setNeedValidate(boolean needValidate) {
		this.needValidate = needValidate;
	}

	public boolean isNeedCalculateAfterCalibrate() {
		return needCalculateAfterCalibrate;
	}

	public void setNeedCalculateAfterCalibrate(boolean needCalculateAfterCalibrate) {
		this.needCalculateAfterCalibrate = needCalculateAfterCalibrate;
	}

	public long getMaxProgramV() {
		return maxProgramV;
	}

	public void setMaxProgramV(long maxProgramV) {
		this.maxProgramV = maxProgramV;
	}

	public long getMaxProgramI() {
		return maxProgramI;
	}

	public void setMaxProgramI(long maxProgramI) {
		this.maxProgramI = maxProgramI;
	}

	public List<CalibratePlanMode> getModes() {
		return modes;
	}

	public void setModes(List<CalibratePlanMode> modes) {
		this.modes = modes;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public void appendMode(CalibratePlanMode pm) {

		modes.add(pm);
	}

	public void appendDot(CalibratePlanMode mode, CalibratePlanDot dot) {

		mode.dots.add(dot);
	}

	@Override
	public void encode() {

		data.add((byte) (needValidate ? 1 : 0));
		data.add((byte) (needCalculateAfterCalibrate ? 1 : 0));
		data.addAll(Arrays.asList(ProtocolUtil.split(maxProgramV, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(maxProgramI, 3, true)));
		data.add((byte) modes.size());
		for (CalibratePlanMode mode : modes) {

			data.add((byte) mode.mode.ordinal());
			data.add((byte) mode.level);
			data.add((byte) mode.pole.ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.pKMin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.pKMax), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.pBMin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.pBMax), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.adcKMin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.adcKMax), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.adcBMin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.adcBMax), 2, true)));

			// 以下4项cv有效
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.checkAdcKMin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.checkAdcKMax), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.checkAdcBmin), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (mode.checkAdcBmax), 2, true)));

			data.add((byte) mode.dots.size());
			for (CalibratePlanDot dot : mode.dots) {

				data.addAll(Arrays.asList(ProtocolUtil.split(dot.da, 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcMin), 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adcMax), 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meterMin), 2, true)));
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.meterMax), 2, true)));
			}

		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		needValidate = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		needCalculateAfterCalibrate = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		maxProgramV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		maxProgramI = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;

		int modeCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < modeCount; n++) {

			CalibratePlanMode mode = new CalibratePlanMode();
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > CalMode.values().length - 1) {

				throw new RuntimeException("invalid mode code :" + code);
			}
			mode.mode = CalMode.values()[code];

			mode.level = ProtocolUtil.getUnsignedByte(data.get(index++));

			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > Pole.values().length - 1) {

				throw new RuntimeException("invalid pole code :" + code);
			}
			mode.pole = Pole.values()[code];

			mode.pKMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.pKMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.pBMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.pBMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.adcKMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.adcKMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.adcBMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.adcBMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			mode.checkAdcKMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.checkAdcKMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.checkAdcBmin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			mode.checkAdcBmax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;

			// 计量点数量
			int dotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
			for (int i = 0; i < dotCount; i++) {

				CalibratePlanDot dot = new CalibratePlanDot();
				dot.da = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.adcMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.adcMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.meterMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				dot.meterMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
				index += 2;
				appendDot(mode, dot);
			}
			appendMode(mode);

		}
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalibratePlanCode;
	}

	@Override
	public String toString() {
		return "CalibratePlanData [needValidate=" + needValidate + ", needCalculateAfterCalibrate="
				+ needCalculateAfterCalibrate + ", maxProgramV=" + maxProgramV + ", maxProgramI=" + maxProgramI
				+ ", modes=" + modes + "]";
	}

}
