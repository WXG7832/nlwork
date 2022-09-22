package com.nltecklib.protocol.fuel.protect;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WorkMode;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;

/**
 * 保护板工作模式协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class PBoardWorkModeData extends Data implements Responsable, Queryable, Configable {
    private WorkMode mode;

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
	return ProtectCode.WORK_MODE_CODE;
    }

    @Override
    public String toString() {
	return "PBoardWorkModeData [boardNum=" + boardNum + ", mode=" + mode + "]";
    }

}
