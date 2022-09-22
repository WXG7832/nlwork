package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

// 校准箱上报工作完成
public class WORK_COMPLETE extends Data implements Queryable, Responsable {

    private boolean autoMatch;
    private boolean calibrate;
    private boolean measure;

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

    @Override
    public void encode() {

	data.add((byte) (autoMatch ? 1 : 0));
	data.add((byte) (calibrate ? 1 : 0));
	data.add((byte) (measure ? 1 : 0));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	autoMatch = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	calibrate = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	measure = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.WORK_COMPLETE;
    }

    public boolean isAutoMatch() {
	return autoMatch;
    }

    public void setAutoMatch(boolean autoMatch) {
	this.autoMatch = autoMatch;
    }

    public boolean isCalibrate() {
	return calibrate;
    }

    public void setCalibrate(boolean calibrate) {
	this.calibrate = calibrate;
    }

    public boolean isMeasure() {
	return measure;
    }

    public void setMeasure(boolean measure) {
	this.measure = measure;
    }

}
