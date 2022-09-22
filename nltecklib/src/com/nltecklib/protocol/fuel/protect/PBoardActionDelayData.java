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
 * 保护板报警后动作延迟时间——0x07
 * 
 * @author caichao_tang
 *
 */
public class PBoardActionDelayData extends Data implements Configable, Queryable, Responsable {
    private int n2Delay;
    private int transducerDelay;

    public int getN2Delay() {
	return n2Delay;
    }

    public void setN2Delay(int n2Delay) {
	this.n2Delay = n2Delay;
    }

    public int getTransducerDelay() {
	return transducerDelay;
    }

    public void setTransducerDelay(int transducerDelay) {
	this.transducerDelay = transducerDelay;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split(n2Delay, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(transducerDelay, 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	n2Delay = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	transducerDelay = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return ProtectCode.ACTION_DELAY;
    }

    @Override
    public String toString() {
	return "PBoardActionDelayData [n2Delay=" + n2Delay + ", transducerDelay=" + transducerDelay + "]";
    }

}
