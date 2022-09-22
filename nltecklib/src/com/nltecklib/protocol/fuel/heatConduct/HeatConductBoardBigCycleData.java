package com.nltecklib.protocol.fuel.heatConduct;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 导热罐控制板大循环状态协议数据
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardBigCycleData extends Data implements Responsable, Queryable {

    private boolean isBigCycle;

    public boolean isBigCycle() {
	return isBigCycle;
    }

    public void setBigCycle(boolean isBigCycle) {
	this.isBigCycle = isBigCycle;
    }

    @Override
    public void encode() {
	data.add((byte) (isBigCycle ? 1 : 0));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	isBigCycle = data.get(0) == 1 ? true : false;
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.BIG_CYCLE;
    }

}
