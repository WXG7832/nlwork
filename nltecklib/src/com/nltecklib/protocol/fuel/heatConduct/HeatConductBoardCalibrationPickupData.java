package com.nltecklib.protocol.fuel.heatConduct;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.camera.Environment.State;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 导热罐控制板校准采集协议数据
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardCalibrationPickupData extends Data implements ComponentSupportable, Responsable, Queryable {
    /**
     * 表示数据稳定，ON 表示稳定， OFF 表示不稳定
     */
    private State ready = State.OFF;
    private double ADCValue;

    public State getReady() {
	return ready;
    }

    public void setReady(State ready) {
	this.ready = ready;
    }

    public double getADCValue() {
	return ADCValue;
    }

    public void setADCValue(double aDCValue) {
	ADCValue = aDCValue;
    }

    @Override
    public void encode() {
	data.add((byte) ready.ordinal());
	data.addAll(Arrays.asList(ProtocolUtil.split((long) ADCValue * 100, 3, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	ready = State.values()[data.get(index)];
	ADCValue = ProtocolUtil.compose(data.subList(index + 1, index + 4).toArray(new Byte[0]), true) / 100.0;
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.CALIBRATION_PICKUP;
    }

}
