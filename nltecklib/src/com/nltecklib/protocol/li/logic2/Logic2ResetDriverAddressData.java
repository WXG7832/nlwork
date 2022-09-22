package com.nltecklib.protocol.li.logic2;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年1月5日 下午4:59:14
* 重新设置驱动板地址
*/
public class Logic2ResetDriverAddressData extends Data implements Configable, Queryable, Responsable {
   
	private int  driverAddress;
	
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
		data.add((byte) driverAddress);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex =ProtocolUtil.getUnsignedByte(data.get(index++));
		driverAddress = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.ResetAddress;
	}

	public int getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(int driverAddress) {
		this.driverAddress = driverAddress;
	}
	
	
	

}
