package com.nltecklib.protocol.lab.pickup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ConnectState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电芯通道物理连接断开协议
 * @author Administrator
 *
 */
public class PConnectData extends Data implements Configable, Queryable, Responsable {
    
	private ConnectState  connectState = ConnectState.DISCONNECT;
	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) connectState.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > ConnectState.values().length - 1) {

			throw new RuntimeException("error connect state code :" + code);
		}
		connectState = ConnectState.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.ConnectCode;
	}

	public ConnectState getConnectState() {
		return connectState;
	}

	public void setConnectState(ConnectState connectState) {
		this.connectState = connectState;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "ConnectData [connectState=" + connectState + "]";
	}
	
	

}
