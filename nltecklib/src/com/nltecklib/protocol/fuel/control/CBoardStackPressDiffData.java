package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 控制板——电堆压力差——0x11
 * 
 * @author caichao_tang
 *
 */
public class CBoardStackPressDiffData extends Data implements Configable, Queryable, Responsable {
    private double stackPressDiff;// 保留1位小数

    public double getStackPressDiff() {
	return stackPressDiff;
    }

    public void setStackPressDiff(double stackPressDiff) {
	this.stackPressDiff = stackPressDiff;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stackPressDiff * 10), 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	stackPressDiff = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10.0;
    }

    @Override
    public Code getCode() {
	return ControlCode.STACK_PRESSURE_DIFFERENCE;
    }

    @Override
    public String toString() {
	return "CBoardStackPressDiff [stackPressDiff=" + stackPressDiff + "]";
    }

}
