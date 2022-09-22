package com.nltecklib.protocol.fuel.heatConduct;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;

/**
 * 导热罐控制板电磁阀采集配置协议
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardSovData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {

    /**
     * 电磁阀开关状态
     */
    private State state = State.OFF;

    public State getState() {
	return state;
    }

    public void setState(State state) {
	this.state = state;
    }

    @Override
    public void encode() {
	data.add((byte) state.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	state = State.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.SOV;
    }

}
