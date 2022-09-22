package com.nltecklib.protocol.li.logic;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * Õ£÷π¬ﬂº≠∞ÂÀ˘”–µÁ–æ≤‚ ‘
 * @author Administrator
 *
 */
public class LogicStopData extends Data implements Configable, Responsable {

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
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));

	}

	@Override
	public Code getCode() {
		
		return LogicCode.StopCode;
	}

}
