package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 0x26 –æ∆¨ID∂¡»°
 * 
 * @author caichao_tang
 *
 */
public class Logic2ReadChipIdData extends Data implements Responsable, Queryable {
    private static final int UUID_LENGTH = 12;
    private String logicId;
    private List<String> driverIdList = new ArrayList<>();

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
	for (int i = 0; i < UUID_LENGTH; i++) {
	    data.add((byte) Integer.parseInt(logicId.substring(i, i + 2), 16));
	}
	data.add((byte) driverIdList.size());
	for (String driverId : driverIdList) {
	    for (int i = 0; i < UUID_LENGTH; i++) {
		data.add((byte) Integer.parseInt(driverId.substring(i, i + 2), 16));
	    }
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
	    if (string.length() < 2)
		string = "0" + string;
	    stringBuilder.append(string);
	}
	logicId = stringBuilder.toString();

	int driverChipNum = ProtocolUtil.getUnsignedByte(data.get(index++));

	for (int i = 0; i < driverChipNum; i++) {
	    stringBuilder = new StringBuilder();
	    for (int j = 0; j < UUID_LENGTH; j++) {
		String string = Integer.toHexString(ProtocolUtil.getUnsignedByte(data.get(index++)));
		if (string.length() < 2)
		    string = "0" + string;
		stringBuilder.append(string);
	    }
	    driverIdList.add(stringBuilder.toString());
	}

    }

    @Override
    public Code getCode() {
	return Logic2Code.READ_CHIP_ID;
    }

    public String getLogicId() {
	return logicId;
    }

    public void setLogicId(String logicId) {
	this.logicId = logicId;
    }

    public List<String> getDriverIdList() {
	return driverIdList;
    }

    public void setDriverIdList(List<String> driverIdList) {
	this.driverIdList = driverIdList;
    }

}
