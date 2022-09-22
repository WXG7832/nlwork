package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

public class MEASURE_DOT extends Data implements Configable, Queryable, Responsable {

    private boolean enabled = false; // 启用
    private CalMode stepType; // 工步类型
    private byte currentRange; // 电流量程档位
    private Pole pole; // 极性
    private double measDot; // 计量点, 即目标输出电压/电流

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

	data.add((byte) driverIndex); // 校准板号
	if (isReverseDriverChnIndex()) {

	    chnIndex = Data.getDriverChnCount() - 1 - chnIndex;
	}
	data.add((byte) chnIndex); // 通道号
	data.add((byte) (enabled ? 0x01 : 0x00));
	data.add((byte) stepType.ordinal());
	data.add(currentRange);
	data.add((byte) pole.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (measDot * 100), 3, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	// 通道号
	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (isReverseDriverChnIndex()) {

	    chnIndex = Data.getDriverChnCount() - 1 - chnIndex;
	}

	// 启用
	enabled = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	// 工步类型
	int idx = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (idx > CalMode.values().length - 1) {

	    throw new RuntimeException("error step type: " + idx);
	}
	stepType = CalMode.values()[idx];

	// 电流量程
	currentRange = data.get(index++);

	// 极性
	idx = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (idx > Pole.values().length - 1) {

	    throw new RuntimeException("error pole: " + idx);
	}
	pole = Pole.values()[idx];

	measDot = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalBoardCode.MEASURE_DOT;
    }

    public Pole getPole() {
	return pole;
    }

    public void setPole(Pole pole) {
	this.pole = pole;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public CalMode getStepType() {
	return stepType;
    }

    public void setStepType(CalMode stepType) {
	this.stepType = stepType;
    }

    public byte getCurrentRange() {
	return currentRange;
    }

    public void setCurrentRange(byte currentRange) {
	this.currentRange = currentRange;
    }

    public double getMeasDot() {
	return measDot;
    }

    public void setMeasDot(double measDot) {
	this.measDot = measDot;
    }

}
