package com.nltecklib.protocol.lab.backup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.SetChnState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BChnWorkModeData extends Data implements Configable, Queryable, Responsable {

	private SetChnState chnState = SetChnState.WORK;

	public SetChnState getChnState() {
		return chnState;
	}

	public void setChnState(SetChnState chnState) {
		this.chnState = chnState;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		data.add((byte) chnState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if (code > SetChnState.values().length - 1) {

			throw new RuntimeException("error chn state code :" + code);
		}
		chnState = SetChnState.values()[code];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.ChnWorkModeCode;
	}

	@Override
	public String toString() {
		return "ChnWorkModeData [chnState=" + chnState + "]";
	}
	
	

}
