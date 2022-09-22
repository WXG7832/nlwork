package com.nltecklib.protocol.plc.pief44;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.protocol.plc.pief44.PIEF44ICControlData.ControlState;
import com.nltecklib.protocol.plc.pief44.PIEF44ICControlData.PIEFPlcDataCode;

/**
 *	 »úÆ÷¿ØÖÆ×´Ì¬\
 * @author Administrator
 *
 */
public class PIEF44AGControlData extends PlcData {
	
	private static final String DEFAULT_AREA = "WR";
	private PIEFPlcDataCode pIEFPlcDataCode;
	private ControlState controlState;
	
	public byte[] encode(){
		String memory = "";
		if(pIEFPlcDataCode.getCode() == PIEFPlcDataCode.AutoState.getCode()){
			memory = area + "." + pIEFPlcDataCode.getCode() + ".00";
		} else {
			memory = area + "." + pIEFPlcDataCode.getCode() + "." + (fixtureIndex + 4);
		}
		byte[] data = null;
		if(!isRead){
			data = new byte[]{(byte) controlState.ordinal()};
		}
		return encode(memory, isBit, isRead, data);
	}
	
	public ControlState controlDecode(byte[] data){
		return ControlState.values()[data[0]];
	}
	
	public PIEF44AGControlData() {
		super();
		isBit = true;
		area = DEFAULT_AREA;
	}

	public PIEF44AGControlData(PIEFPlcDataCode pIEFPlcDataCode, int fixtureIndex) {
		super();
		this.pIEFPlcDataCode = pIEFPlcDataCode;
		this.fixtureIndex = fixtureIndex;
		isBit = true;
		area = DEFAULT_AREA;
	}
	
	public PIEF44AGControlData(PIEFPlcDataCode pIEFPlcDataCode, int fixtureIndex, boolean isRead, ControlState controlState) {
		super();
		this.pIEFPlcDataCode = pIEFPlcDataCode;
		this.fixtureIndex = fixtureIndex;
		this.controlState = controlState;
		this.isRead = isRead;
		isBit = true;
		area = DEFAULT_AREA;
	}


	public PIEFPlcDataCode getPlcDataCode() {
		return pIEFPlcDataCode;
	}

	public void setPlcDataCode(PIEFPlcDataCode pIEFPlcDataCode) {
		this.pIEFPlcDataCode = pIEFPlcDataCode;
	}

	public ControlState getControlState() {
		return controlState;
	}

	public void setControlState(ControlState controlState) {
		this.controlState = controlState;
	}
	
	
	
}
