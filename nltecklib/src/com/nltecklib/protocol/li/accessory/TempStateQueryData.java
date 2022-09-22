package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class TempStateQueryData extends Data implements Queryable, Responsable {
    
	private byte runFlag;
	private byte errFlag;
	private int  count;
	
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
		
		data.add((byte) count);
		data.add(runFlag);
        data.add(errFlag);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		runFlag = data.get(index++);
		errFlag = data.get(index++);

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.MeterStateCode;
	}

	public byte getErrFlag() {
		return errFlag;
	}

	public void setErrFlag(byte flag) {
		this.errFlag = flag;
	}
	
	public byte getRunFlag() {
		return runFlag;
	}

	public void setRunFlag(byte runFlag) {
		this.runFlag = runFlag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "TempStateQueryData [runFlag=" + runFlag + ", errFlag=" + errFlag + ", count=" + count + "]";
	}
	
	

}
