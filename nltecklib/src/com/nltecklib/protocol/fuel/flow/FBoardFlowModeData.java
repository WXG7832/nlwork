package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ChnSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.Mode;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.Pole;

/**
 * 流量板流量模式协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class FBoardFlowModeData extends Data implements ChnSupportable, Configable, Responsable, Queryable {
    private Mode mode;

    public Pole getPole() {
	return Pole.values()[chnNum];
    }

    public void setPole(Pole pole) {
	this.chnNum = pole.ordinal();
    }

    public Mode getMode() {
	return mode;
    }

    public void setMode(Mode mode) {
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
	mode = Mode.values()[data.get(index)];
	index++;
    }

    @Override
    public Code getCode() {
	return FlowCode.FLOW_MODE_CODE;
    }

    @Override
    public String toString() {
	return "FBoardFlowModeData [pole=" + Pole.values()[chnNum] + ", mode=" + mode + "]";
    }

}
