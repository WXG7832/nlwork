package com.nltecklib.protocol.fuel.control;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.util.ProtocolUtil;
@Deprecated
public class CBoardN2Data extends Data implements BoardNoSupportable, Configable, Responsable {
    private int second;

    public int getSecond() {
	return second;
    }

    public void setSecond(int second) {
	this.second = second;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split((long) second, 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	second = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return ControlCode.N2_CODE;
    }

    @Override
    public String toString() {
	return "CBoardN2Data [boardNum=" + boardNum + ", second=" + second + "]";
    }

}
