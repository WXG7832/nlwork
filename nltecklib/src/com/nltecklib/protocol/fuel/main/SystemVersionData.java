package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控系统版本协议数据——0x12
 * 
 * @author caichao_tang
 *
 */
public class SystemVersionData extends Data implements Configable, Responsable, Queryable {
    private int software;
    private int hardware;

    public int getSoftware() {
	return software;
    }

    public void setSoftware(int software) {
	this.software = software;
    }

    public int getHardware() {
	return hardware;
    }

    public void setHardware(int hardware) {
	this.hardware = hardware;
    }

    @Override
    public void encode() {
	data.add((byte) software);
	data.add((byte) hardware);
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	software = ProtocolUtil.getUnsignedByte(data.get(index++));
	hardware = ProtocolUtil.getUnsignedByte(data.get(index++));
    }

    @Override
    public Code getCode() {
	return MainCode.SYSTEM_VERSION_CODE;
    }

    @Override
    public String toString() {
	return "SystemVersionData [result=" + result + ", orient=" + orient + ", software=" + software + ", hardware=" + hardware + "]";
    }

}
