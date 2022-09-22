package com.nltecklib.protocol.li.accessory;

import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电源控制协议0x25
 * 
 * @author caichao_tang
 *
 */
public class PowerSwitchData2 extends Data implements Configable, Queryable, Responsable {
    private PowerState state = PowerState.OFF;

    @Override
    public boolean supportUnit() {
	return true;// 地址
    }

    @Override
    public boolean supportDriver() {
	return true; // 开关组号
    }

    @Override
    public boolean supportChannel() {
	return true;// 电源类型（0充放电源，1辅助电源）
    }

    @Override
    public void encode() {
	data.add((byte) unitIndex);
	data.add((byte) driverIndex);
	data.add((byte) chnIndex);
	data.add((byte) state.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (code > PowerState.values().length - 1) {
	    throw new RuntimeException("error power state code : " + code);
	}
	state = PowerState.values()[code];
    }

    @Override
    public Code getCode() {
	return AccessoryCode.POWER_SWITCH2;
    }

    public PowerState getState() {
	return state;
    }

    public void setState(PowerState state) {
	this.state = state;
    }

}
