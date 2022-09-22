package com.nltecklib.protocol.li.accessory;

import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 0x31¼±Í£×´Ì¬
 * 
 * @author caichao_tang
 *
 */
public class EmergencyData extends Data implements Queryable, Responsable {
    private boolean isEmergency;

    @Override
    public boolean supportUnit() {
	return false;
    }

    @Override
    public boolean supportDriver() {
	return true;// °åµØÖ·
    }

    @Override
    public boolean supportChannel() {
	return false;
    }

    @Override
    public void encode() {
	data.add((byte) driverIndex);
	data.add((byte) (isEmergency ? 1 : 0));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	isEmergency = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
    }

    @Override
    public Code getCode() {
		return AccessoryCode.EmergencyStopCode;
    }

    public boolean isEmergency() {
	return isEmergency;
    }

    public void setEmergency(boolean isEmergency) {
	this.isEmergency = isEmergency;
    }

}
