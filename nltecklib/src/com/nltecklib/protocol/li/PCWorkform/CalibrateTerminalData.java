package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalibrateTerminalData extends Data implements Queryable, Configable, Responsable {

	private CalibrateTerminal calibrateTerminal = CalibrateTerminal.PC;

	public enum CalibrateTerminal {
		PC, Screen
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		data.add((byte) calibrateTerminal.ordinal());

	}
	
	

	public CalibrateTerminal getCalibrateTerminal() {
		return calibrateTerminal;
	}

	public void setCalibrateTerminal(CalibrateTerminal calibrateTerminal) {
		this.calibrateTerminal = calibrateTerminal;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data=encodeData;
		int index=0;
		int code=ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code>CalibrateTerminal.values().length-1) {
			throw new RuntimeException("CalibrateTerminal code error :"+code);
		}
		calibrateTerminal=CalibrateTerminal.values()[code];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalibrateTerminalCode;
	}

	@Override
	public String toString() {
		return "CalibrateTerminalData [calibrateTerminal=" + calibrateTerminal + "]";
	}

}
