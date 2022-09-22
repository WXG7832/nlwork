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
 * 校准板连接万用表
 * 
 * @author Administrator
 *
 */
public class CONNECT_METER extends Data implements Queryable, Configable, Responsable {

    private boolean connect; // 连接/断开万用表

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

	data.add((byte) driverIndex); // 校准板序号
	data.add((byte) (connect ? 1 : 0));
    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalBoardCode.CONNECT_METER;
    }

    public boolean isConnected() {
	return connect;
    }

    public void setConnect(boolean connect) {
	this.connect = connect;
    }

}
