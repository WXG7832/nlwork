package com.nltecklib.protocol.fuel.heatConduct;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.TransducerState;

/**
 * 导热罐控制板变频器协议数据
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardTransducerData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    /**
     * 变频器状态
     */
    private TransducerState state = TransducerState.OFF;

    public TransducerState getState() {
	return state;
    }

    public void setState(TransducerState state) {
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
	state = TransducerState.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.TRANSDUCER;
    }

}
