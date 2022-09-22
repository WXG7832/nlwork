package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.List;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;

// 手动对接, 校准板与设备分区配对
public class MANUAL_MATCH extends Data {

    private byte calBoardIdx = -1; // -1=未对接

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
	// TODO Auto-generated method stub

    }

    @Override
    public void decode(List<Byte> encodeData) {
	// TODO Auto-generated method stub

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.MANUAL_MATCH;
    }

    public byte getCalBoardIdx() {
	return calBoardIdx;
    }

    public void setCalBoardIdx(byte calBoardIdx) {
	this.calBoardIdx = calBoardIdx;
    }

}
