package com.nltecklib.protocol.fuel.voltage;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;

/**
 * 电压采集板错误状态处理-0x08 仅支持配置
 * 
 * @author caichao_tang
 *
 */
public class VBoardExceptionStateDealData extends Data implements BoardNoSupportable, Configable, Responsable, Queryable {
    private boolean controlRelay;

    public boolean isControlRelay() {
	return controlRelay;
    }

    public void setControlRelay(boolean controlRelay) {
	this.controlRelay = controlRelay;
    }

    @Override
    public void encode() {
	data.add((byte) (controlRelay == false ? 0 : 1));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	controlRelay = data.get(0) == 0 ? false : true;
    }

    @Override
    public Code getCode() {
	return VolCode.EXCEPTION_DEAL;
    }

    @Override
	public String toString() {
		return "VBoardExceptionStateDealData [controlRelay=" + controlRelay + "]";
	}

}
