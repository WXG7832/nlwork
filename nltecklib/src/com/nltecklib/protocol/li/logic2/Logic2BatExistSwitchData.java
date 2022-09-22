package com.nltecklib.protocol.li.logic2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年2月15日 下午2:03:55
* 类说明
*/
public class Logic2BatExistSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private SwitchState  switchState = SwitchState.OFF;
	
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
	
	
	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) switchState.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > SwitchState.values().length - 1) {
			
			throw new RuntimeException("error switch state code:" + code);
		}
		switchState = SwitchState.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.BatExistSwitchCode;
	}

}
