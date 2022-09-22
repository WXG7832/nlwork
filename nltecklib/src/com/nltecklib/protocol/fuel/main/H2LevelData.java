package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;
@Deprecated
public class H2LevelData extends Data implements Configable, Responsable, Queryable {
    private int level;

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }

    @Override
    public void encode() {
	data.add((byte) level);
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	level = ProtocolUtil.getUnsignedByte(data.get(index++));
    }

    @Override
    public Code getCode() {
    	return null;
//	return MainCode.H2_LEVEL_CODE;
    }

    @Override
    public String toString() {
	return "H2LevelData [level=" + level + "]";
    }

}
