package com.nltecklib.protocol.lab.backup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.ConnectState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 直流母线继电器控制
 * @author Administrator
 *
 */
public class BDcRelayData extends Data implements Configable, Queryable, Responsable {
    
	private ConnectState  connectState = ConnectState.DISCONNECT;
	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
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
		return BackupCode.DcRelayCode;
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
		return "DcRelayData [connectState=" + connectState + "]";
	}
	
	

}
