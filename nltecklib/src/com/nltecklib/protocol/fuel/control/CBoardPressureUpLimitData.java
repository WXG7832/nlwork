package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 控制板，压力报警上限配置
 * 
 * @author caichao_tang
 *
 */
public class CBoardPressureUpLimitData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    private double pressureUpLimit;// 保留1位小数，单位kPa

    public double getPressureUpLimit() {
	return pressureUpLimit;
    }

    public void setPressureUpLimit(double pressureUpLimit) {
	this.pressureUpLimit = pressureUpLimit;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (pressureUpLimit * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	pressureUpLimit = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return ControlCode.PRESSURE_UP_LIMIT;
    }

    @Override
    public String toString() {
	return "FBoardPressureUpLimit [pressureUpLimit=" + pressureUpLimit + "]";
    }

}
