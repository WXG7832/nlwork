package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流量板氢气等级协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class FBoardH2LevelData extends Data implements Configable, Responsable, Queryable {
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
	return FlowCode.H2_LEVEL;
    }

    @Override
    public String toString() {
	return "FBoardH2LevelData [level=" + level + "]";
    }

}
