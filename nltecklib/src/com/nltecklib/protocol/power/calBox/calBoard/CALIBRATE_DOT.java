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

public class CALIBRATE_DOT extends Data implements Queryable, Configable, Responsable {

    private boolean enabled = false; // 启用
    private CalMode stepType; // 工步类型
    private byte currentRange; // 电流量程档位
    private Pole pole; // 极性
    private int voltDA;// 电压量化值 0-65535
    private int currentDA;// 电流量化值 0-65535

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
	data.addAll(Arrays.asList(ProtocolUtil.split(voltDA, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(currentDA, 2, true)));

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

	voltDA = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;

	currentDA = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;

    }

    @Override
    public Code getCode() {

	return CalBoardCode.CALIBRATE_DOT;
    }

    public Pole getPole() {
	return pole;
    }

    public void setPole(Pole pole) {
	this.pole = pole;
    }

    @Override
    public boolean supportDriver() {

	return true;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return true;
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

    public int getVoltDA() {
	return voltDA;
    }

    public void setVoltDA(int voltDA) {
	this.voltDA = voltDA;
    }

    public int getCurrentDA() {
	return currentDA;
    }

    public void setCurrentDA(int currentDA) {
	this.currentDA = currentDA;
    }

}
