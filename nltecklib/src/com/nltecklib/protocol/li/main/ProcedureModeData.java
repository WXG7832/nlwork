package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ProcedureModeData extends Data implements Configable, Queryable, Responsable {
    
	private ProcedureMode procedureMode;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		
		data.add((byte) procedureMode.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ProcedureMode.values().length - 1) {
			
			throw new RuntimeException("error procedure mode code :" + code);
		}
		
		procedureMode = ProcedureMode.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.ProcedureModeCode;
	}

	public ProcedureMode getProcedureMode() {
		return procedureMode;
	}

	public void setProcedureMode(ProcedureMode procedureMode) {
		this.procedureMode = procedureMode;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
