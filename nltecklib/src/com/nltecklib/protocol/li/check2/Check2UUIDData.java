package com.nltecklib.protocol.li.check2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 0x1d –æ∆¨UUID∂¡»°
 * 
 * @author caichao_tang
 *
 */
public class Check2UUIDData extends Data implements Queryable, Configable, Responsable {
    private static final int UUID_LENGTH = 16;
    private String uuid;

    @Override
    public boolean supportUnit() {
	return true;
    }

    @Override
    public boolean supportDriver() {
	return false;
    }

    @Override
    public boolean supportChannel() {
	return false;
    }

    @Override
    public void encode() {
	data.add((byte) unitIndex);
	int index = 0;
	for (int i = 0; i < UUID_LENGTH; i++) {
	    data.add((byte) Integer.parseInt(uuid.substring(index, index = index + 2), 16));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	StringBuilder stringBuilder = new StringBuilder();
	for (int i = 0; i < UUID_LENGTH; i++) {
	    String string = Integer.toHexString(ProtocolUtil.getUnsignedByte(data.get(index++)));
	    if (string.length() < 2) {
		string = "0" + string;
	    }
	    stringBuilder.append(string);
	}
	uuid = stringBuilder.toString();
    }

    @Override
    public Code getCode() {
	return Check2Code.UUID_CODE;
    }

    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

}
