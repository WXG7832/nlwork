package com.nltecklib.protocol.fuel.control;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WorkMode;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;

public class CBoardWorkModeData extends Data implements Responsable, Queryable, Configable {
    private WorkMode mode = WorkMode.STOP;

    public WorkMode getMode() {
	return mode;
    }

    public void setMode(WorkMode mode) {
	this.mode = mode;
    }

    @Override
    public void encode() {
	data.add((byte) mode.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	mode = WorkMode.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return ControlCode.WORK_MODE_CODE;
    }

    @Override
    public String toString() {
	return "CBoardWorkModeData [boardNum=" + boardNum + ", mode=" + mode + "]";
    }

}
