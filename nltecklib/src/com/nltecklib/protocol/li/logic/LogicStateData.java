package com.nltecklib.protocol.li.logic;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicState;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 修改分区标识状态
 * @author Administrator
 *
 */
public class LogicStateData extends Data implements Configable,Responsable{
    
	private LogicState startupState = LogicState.UDT;
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		data.add((byte)startupState.getCode());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex =  ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		int val = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		for(int i = 0 ; i < LogicState.values().length ; i++) {
			
			if(LogicState.values()[i].getCode() == val) {
				
				startupState = LogicState.values()[i];
				return;
			}
		}
		startupState = null;

	}

	@Override
	public Code getCode() {
		
		return LogicCode.StateCode;
	}

	public LogicState getStartupState() {
		return startupState;
	}

	public void setStartupState(LogicState startupState) {
		this.startupState = startupState;
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
