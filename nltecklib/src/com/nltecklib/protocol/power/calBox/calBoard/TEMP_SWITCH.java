package com.nltecklib.protocol.power.calBox.calBoard;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 开关校准板温控
 * 
 * @author Administrator
 *
 */
public class TEMP_SWITCH extends Data implements Queryable, Configable, Responsable {

    private boolean enabled;// 开关
    private byte temperature;// 温度

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

	data.add((byte) (enabled ? 1 : 0));
	data.add(temperature);

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	enabled = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	temperature = data.get(index++);

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalBoardCode.TEMP_SWITCH;
    }

    public byte getTemperature() {
	return temperature;
    }

    public void setTemperature(byte temperature) {
	this.temperature = temperature;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

}
