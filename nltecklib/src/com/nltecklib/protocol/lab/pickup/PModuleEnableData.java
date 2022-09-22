package com.nltecklib.protocol.lab.pickup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PModuleEnableData extends Data implements Configable, Queryable, Responsable {
    
	private SwitchState   state = SwitchState.CLOSE;

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) state.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > SwitchState.values().length - 1) {

			throw new RuntimeException("error pole code :" + code);
		}
		state = SwitchState.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.ModuleEnableCode;
	}

	public SwitchState getState() {
		return state;
	}

	public void setState(SwitchState state) {
		this.state = state;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "ModuleEnableData [state=" + state + "]";
	}
	
	

}
