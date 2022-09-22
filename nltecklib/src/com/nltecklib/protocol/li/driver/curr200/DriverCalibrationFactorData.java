package com.nltecklib.protocol.li.driver.curr200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准系数写入
 * 
 * @author admin
 *
 */
public class DriverCalibrationFactorData extends Data implements Configable, Queryable, Responsable {

//	public static final int ADC_BIT_COUNT = 2;
//	public static final int K_BIT_COUNT = 7;
//	public static final int B_BIT_COUNT = 7;

    // 膜片编号
    private int diapIndex;

    public int getDiapIndex() {
	return diapIndex;
    }

    public void setDiapIndex(int diapIndex) {
	this.diapIndex = diapIndex;
    }

    public static class CalDot implements Comparable {

	public CalMode mode;
	public boolean POLE_POS;// 正极
	public double adc; // 用于显示
	public double adcK;
	public double adcB;
	public long da;
	public double meter; // 表值
	public double programK;
	public double programB;
	public int level; // 精度档位，精度越高数字越大

	public CalDot() {
	}

	public CalDot(CalMode mode, boolean pole, double meter, double adc, double adcK, double adcB, long da, double programK, double programB) {
	    this.mode = mode;
	    this.POLE_POS = pole;
	    this.meter = meter;
	    this.adc = adc;
	    this.adcK = adcK;
	    this.adcB = adcB;
	    this.da = da;
	    this.programK = programK;
	    this.programB = programB;
	}

	@Override
	public int compareTo(Object o) {

	    CalDot dot = (CalDot) o;
	    if (this.mode != dot.mode) {

		return this.mode.ordinal() - dot.mode.ordinal();
	    } else {

		// 比较极性
		if (this.POLE_POS != dot.POLE_POS) {

		    return (dot.POLE_POS ? 1 : 0) - (this.POLE_POS ? 1 : 0); // 正极性先排
		} else {
		    // 比较计量点
		    return (int) (this.meter - dot.meter);
		}
	    }

	}

	@Override
	public String toString() {
	    return "CalDot [mode=" + mode + ", POLE_POS=" + POLE_POS + ", adc=" + adc + ", adcK=" + adcK + ", adcB=" + adcB + ", da=" + da + ", meter=" + meter + ", programK=" + programK + ", programB=" + programB + ", level=" + level + "]";
	}

    }

    // 校准点集合
    private List<CalDot> dots = new ArrayList<>();

    public void addCalDot(CalDot dot) {

	dots.add(dot);
    }

    public void clearCalDot() {

	dots.clear();
    }

    public int getDotCountByMode(CalMode mode) {

	int count = 0;
	for (CalDot dot : dots) {

	    if (dot.mode == mode) {

		count++;
	    }

	}
	return count;

    }

    public enum CalMode {
	CC, CV, DC, CV2
    }

    @Override
    public boolean supportUnit() {
	// TODO Auto-generated method stub
	return false;
    }

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

	data.add((byte) driverIndex);
	data.add(isReverseDriverChnIndex() ? (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount()) : (byte) chnIndex);

	data.add((byte) diapIndex);
	// 总校准点数
	data.add((byte) dots.size());
	// 各模式校准点数统计
	data.add((byte) getDotCountByMode(CalMode.CC));
	data.add((byte) getDotCountByMode(CalMode.CV));
	data.add((byte) getDotCountByMode(CalMode.DC));
	data.add((byte) getDotCountByMode(CalMode.CV2));

	// Collections.sort(dots); // 排序
	for (CalDot dot : dots) {

	    data.add((byte) (dot.mode.ordinal() + 1)); // 模式 211021协议更改: 步次序号从1开始
	    data.add((byte) (dot.POLE_POS ? 0x01 : 0x00));// 极性
	    data.add((byte) dot.level); // level
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, currentResolution)), 3, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, adcKResolution)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, adcBResolution)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da, 2, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, programKResolution)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, programBResolution)), 4, true)));

	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	// 通道号
	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (isReverseDriverChnIndex()) {

	    chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
	}
	diapIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	int dotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
	index += 4; // 不需要解析模式点数

	for (int n = 0; n < dotCount; n++) {

	    CalDot dot = new CalDot();

	    int code = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
	    if (code > CalMode.values().length - 1) {

		throw new RuntimeException("invalid mode index :" + code);
	    }

	    dot.mode = CalMode.values()[code];

	    dot.POLE_POS = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	    // level
	    dot.level = ProtocolUtil.getUnsignedByte(data.get(index++));

	    dot.adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, currentResolution);
	    index += 3;

	    dot.adcK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, adcKResolution);
	    index += 4;
	    dot.adcB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, adcBResolution);
	    index += 4;
	    dot.da = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
	    index += 2;
	    dot.meter = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
	    index += 3;

	    dot.programK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, programKResolution);
	    index += 4;
	    dot.programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, programBResolution);
	    index += 4;
	    dots.add(dot);

	}
    }

    @Override
    public Code getCode() {
	return DriverCode.Driver200aCalibrationFactorCode;
    }

    public List<CalDot> getDots() {
	return dots;
    }

    public void setDots(List<CalDot> dots) {
	this.dots = dots;
    }

    public CalDot getCalDot() {
	return new CalDot();
    }

}
