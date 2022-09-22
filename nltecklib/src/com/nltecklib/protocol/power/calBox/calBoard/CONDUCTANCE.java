package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CONDUCTANCE extends Data implements Queryable, Responsable {

    private final static int EXP = 8; // 小数位精度
    private double highPrecision;
    private double normalPrecision;

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.add((byte) driverIndex);
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (highPrecision * Math.pow(10, EXP)), 4, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (normalPrecision * Math.pow(10, EXP)), 4, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	highPrecision = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, EXP);
	index += 4;
	normalPrecision = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, EXP);
    }

    @Override
    public Code getCode() {

	return CalBoardCode.CONDUCTANCE;
    }

    public double getHighPrecision() {
	return highPrecision;
    }

    public double getNormalPrecision() {
	return normalPrecision;
    }

    public void setHighPrecision(double highPrecision) {
	this.highPrecision = highPrecision;
    }

    public void setNormalPrecision(double normalPrecision) {
	this.normalPrecision = normalPrecision;
    }

}
