package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准板温度
 * 
 * @author Administrator
 *
 */
public class TEMP extends Data implements Queryable, Responsable {

    private byte temperature;// 温度
    private boolean overheat;// 已超温
    private boolean fanFault;// 风机故障
    private boolean timeout;// 采温超时

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.add(temperature);
	data.add((byte) (overheat ? 1 : 0));
	data.add((byte) (fanFault ? 1 : 0));
	data.add((byte) (timeout ? 1 : 0));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	temperature = data.get(index++);
	overheat = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	fanFault = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	timeout = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalBoardCode.TEMP;
    }

    public byte getTemperature() {
	return temperature;
    }

    public void setTemperature(byte temperature) {
	this.temperature = temperature;
    }

    public boolean isOverheat() {
	return overheat;
    }

    public void setOverheat(boolean overheat) {
	this.overheat = overheat;
    }

    public boolean isFanFault() {
	return fanFault;
    }

    public void setFanFault(boolean fanFault) {
	this.fanFault = fanFault;
    }

    public boolean isTimeout() {
	return timeout;
    }

    public void setTimeout(boolean timeout) {
	this.timeout = timeout;
    }

}
