package com.nltecklib.protocol.fuel.protect;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ChnSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.State;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;

/**
 * 保护板电源开关协议协议数据
 * 
 * @author caichao_tang
 *
 */
public class PBoardPowerData extends Data implements Configable, Responsable, Queryable ,ChnSupportable{

    private State state = State.OFF;
    
    //通道号代替电源地址
    public void setAddress(int address) {
    	chnNum=address;
    }
    
    public int getAddress() {
    	return chnNum;
    }

    public State getState() {
	return state;
    }

    public void setState(State state) {
	this.state = state;
    }

    @Override
    public void encode() {
	if (state != null) {
	    data.add((byte) state.ordinal());
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	state = State.values()[data.get(0)];
    }

    @Override
    public Code getCode() {
	return ProtectCode.POWER_CODE;
    }

    @Override
    public String toString() {
	return "PBoardPowerData [powerAddress=" + getAddress() + ", state=" + state + ", data=" + data + ", boardNum=" + boardNum + ", chnNum=" + chnNum + ", componentCode=" + component + ", result=" + result + ", orient=" + orient + "]";
    }

}
