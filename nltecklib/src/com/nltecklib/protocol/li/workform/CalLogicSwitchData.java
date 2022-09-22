package com.nltecklib.protocol.li.workform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
 * 逻辑板使能开关
 * @author Administrator
 *
 */
public class CalLogicSwitchData extends Data implements Configable, Queryable, Responsable {
    
	private boolean open;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
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
		
		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		data.add((byte) (open ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex  = ProtocolUtil.getUnsignedByte(data.get(index++));
		open      = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		
		return WorkformCode.ModuleSwitchCode;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	

}
