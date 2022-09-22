package com.nltecklib.protocol.lab.adCal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CHN_STATE extends Data implements Configable, Queryable, Responsable {

    private byte state = 0; // 0_work, 1_cal, 2_update

    public static enum ChnState {
	WORK(0x00), CAL(0x01), UPDATE(0x02);

	private int code;

	public int getCode() {

	    return code;
	}

	private ChnState(int funCode) {

	    this.code = funCode;
	}

	public static ChnState valueOf(int code) {

	    for (ChnState temp : ChnState.values()) {
		if (temp.getCode() == code) {
		    return temp;
		}
	    }
	    return null;
	}
    }

    public CHN_STATE() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.add((byte) state);

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	state = (byte) ProtocolUtil.getUnsignedByte(data.get(index));

    }

    @Override
    public Code getCode() {

	return ADCalCode.CHN_STATE;
    }

    public short getState() {

	return state;
    }

    public void setState(byte state) {
	this.state = state;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "ADÍ¨µÀ×´Ì¬(0x01)";
    }

}
