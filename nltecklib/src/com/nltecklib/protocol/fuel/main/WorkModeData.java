package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainWorkMode;

/**
 * 主控板工作模式协议数据——0x11
 * 
 * @author caichao_tang
 *
 */
public class WorkModeData extends Data implements Configable, Responsable, Queryable {

    private MainWorkMode mode;

    public MainWorkMode getMode() {
	return mode;
    }

    public void setMode(MainWorkMode mode) {
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
	mode = MainWorkMode.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return MainCode.WORK_MODE_CODE;
    }

    @Override
	public String toString() {
		return "WorkModeData [mode=" + mode + "]";
	}

}
