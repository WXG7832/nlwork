package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class MATCH_DOT extends Data implements Configable, Queryable, Responsable {

    private boolean enabled = false; // 启用
    private short volt = 0; // 电压量化值 0-50000

    @Override
    public void encode() {

	data.add((byte) driverIndex); // 校准板序号
	data.add(!isReverseDriverChnIndex() ? (byte) chnIndex : (byte) ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount()));
	data.add((byte) (enabled ? 0x01 : 0x00));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (volt * 10), 2, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	chnIndex = !isReverseDriverChnIndex() ? ProtocolUtil.getUnsignedByte(data.get(index++)) : ProtocolUtil.reverseChnIndexInLogic(ProtocolUtil.getUnsignedByte(data.get(index++)), Data.getDriverChnCount());
	enabled = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
	volt = (short) (ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);

    }

    @Override
    public Code getCode() {

	return CalBoardCode.MATCH_DOT;
    }

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public short getVolt() {
	return volt;
    }

    public void setVolt(short volt) {
	this.volt = volt;
    }

}
