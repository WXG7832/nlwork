package com.nltecklib.protocol.lab.backup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.StateIdent;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 采集板工作环境切换协议
 * @author Administrator
 *
 */
public class BWorkEnvData extends Data implements Configable, Queryable, Responsable {
    
	private StateIdent   stateIdent = StateIdent.WORK;
	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) stateIdent.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > StateIdent.values().length - 1) {
			
			throw new RuntimeException("error work env code :" + code);
		}
		
		stateIdent = StateIdent.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.WorkStateCode;
	}


	public StateIdent getStateIdent() {
		return stateIdent;
	}

	public void setStateIdent(StateIdent stateIdent) {
		this.stateIdent = stateIdent;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "WorkEnvData [workEnv=" + stateIdent + "]";
	}
	
	

}
