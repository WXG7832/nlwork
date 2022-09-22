package com.nltecklib.protocol.fuel.temp;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WorkMode;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;

/**
 * 温控板工作模式协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class TBoardWorkModeData extends Data implements BoardNoSupportable, Responsable, Queryable, Configable {
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
	return TempCode.WORK_MODE_CODE;
    }

    @Override
    public String toString() {
	return "TBoardWorkModeData [boardNum=" + boardNum + ", mode=" + mode + "]";
    }

}
