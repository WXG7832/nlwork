package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WorkMode;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;

/**
 * 流量板工作模式协议数据
 * 
 * @author caichao_tang
 *
 */
public class FBoardWorkModeData extends Data implements Responsable, Queryable, Configable {
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
	return FlowCode.WORK_MODE_CODE;
    }

    @Override
    public String toString() {
	return "FBoardWorkModeData [boardNum=" + boardNum + ", mode=" + mode + "]";
    }

}
