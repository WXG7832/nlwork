package com.nltecklib.protocol.fuel.voltage;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VBoardWorkMode;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;

/**
 * 电压采集板工作模式 0x02
 * 
 * @author caichao_tang
 *
 */
public class VBoardVolModeData extends Data implements BoardNoSupportable, Responsable, Queryable, Configable {
    private VBoardWorkMode mode = VBoardWorkMode.AWAIT;

    public VBoardWorkMode getMode() {
	return mode;
    }

    public void setMode(VBoardWorkMode mode) {
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
	mode = VBoardWorkMode.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return VolCode.WORK_MODE_CODE;
    }

    @Override
    public String toString() {
	return "VBoardVolModeData [boardNum=" + boardNum + ", mode=" + mode + "]";
    }

}
