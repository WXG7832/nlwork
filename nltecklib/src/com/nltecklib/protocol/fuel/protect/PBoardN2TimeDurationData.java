package com.nltecklib.protocol.fuel.protect;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 保护板报警后吹扫延时
 * 
 * @author caichao_tang
 *
 */
public class PBoardN2TimeDurationData extends Data implements Configable, Queryable, Responsable {
    private int n2Duration;

    public int getN2Delay() {
	return n2Duration;
    }

    public void setN2Delay(int n2Delay) {
	this.n2Duration = n2Delay;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split(n2Duration, 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	n2Duration = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return ProtectCode.N2_TIME_DURATION_CODE;
    }

    @Override
    public String toString() {
	return "PBoardN2TimeDurationData [n2Duration=" + n2Duration + "]";
    }

}
