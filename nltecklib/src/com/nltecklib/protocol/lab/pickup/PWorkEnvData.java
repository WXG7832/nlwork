package com.nltecklib.protocol.lab.pickup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.WorkEnv;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 采集板工作环境切换协议
 * @author Administrator
 *
 */
public class PWorkEnvData extends Data implements Configable, Queryable, Responsable {
    
	private WorkEnv   workEnv = WorkEnv.WORK;
	

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) workEnv.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > WorkEnv.values().length - 1) {
			
			throw new RuntimeException("error work env code :" + code);
		}
		
		workEnv = WorkEnv.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.WorkEnvCode;
	}

	public WorkEnv getWorkEnv() {
		return workEnv;
	}

	public void setWorkEnv(WorkEnv workEnv) {
		this.workEnv = workEnv;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "WorkEnvData [workEnv=" + workEnv + "]";
	}
	
	

}
