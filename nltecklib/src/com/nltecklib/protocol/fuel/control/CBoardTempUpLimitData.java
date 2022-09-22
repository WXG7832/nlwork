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
 * 控制板，温度报警上限配置 0x10
 * 
 * @author caichao_tang
 *
 */
public class CBoardTempUpLimitData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    private double tempUpLimit;// 保留1位小数，单位摄氏度

    public double getTempUpLimit() {
	return tempUpLimit;
    }

    public void setTempUpLimit(double tempUpLimit) {
	this.tempUpLimit = tempUpLimit;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (tempUpLimit * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	tempUpLimit = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return ControlCode.TEMP_UP_LIMIT;
    }

    @Override
    public String toString() {
	return "CBoardTempUpLimit [tempUpLimit=" + tempUpLimit + "]";
    }

}
