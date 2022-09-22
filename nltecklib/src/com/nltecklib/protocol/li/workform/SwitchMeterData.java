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
 * 通过校准板切换万用表
 * @author Administrator
 *
 */
public class SwitchMeterData extends Data implements Configable, Queryable, Responsable {
   
	
	private int meterIndex; //万用表号，默认为0
	private boolean connected;  //是否连接万用表
	
	@Override
	public boolean supportUnit() {
		
		return false;
	}

	@Override
	public boolean supportDriver() {
		
		return true;
	}

	@Override
	public boolean supportChannel() {
	
		return false;
	}

	@Override
	public void encode() {
		
        data.add((byte) driverIndex); //校准板号
        data.add((byte) meterIndex); //万用表号
        data.add((byte) (connected ? 1 : 0)); //设置连接开关
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		meterIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		connected  = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1 ;
	}

	@Override
	public Code getCode() {
		
		return  WorkformCode.MeterSwitchCode;
	}

	public int getMeterIndex() {
		return meterIndex;
	}

	public void setMeterIndex(int meterIndex) {
		this.meterIndex = meterIndex;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	

}
