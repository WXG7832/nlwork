package com.nltecklib.protocol.fuel.heatConduct;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WorkMode;

/**
 * 导热罐控制板工作模式协议数据
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardWorkModeData extends Data implements Responsable, Queryable, Configable {
    /**
     * 工作模式（stop；test）
     */
    private WorkMode workMode = WorkMode.STOP;

    public WorkMode getWorkMode() {
	return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
	this.workMode = workMode;
    }

    @Override
    public void encode() {
	data.add((byte) workMode.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	workMode = WorkMode.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.WORK_MODE;
    }

}
