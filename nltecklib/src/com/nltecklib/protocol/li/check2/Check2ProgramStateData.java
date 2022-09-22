package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年1月5日 下午1:25:40
* 回检程序烧录状态查询
*/
public class Check2ProgramStateData extends Data implements Queryable, Responsable {
    
	private boolean  checkBurnOk;
	private List<Boolean>  driverBurnOkList = new ArrayList<>();
	
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
		data.add((byte) (checkBurnOk ? 0x01 : 0x00));
		data.add((byte) driverBurnOkList.size());
		for(int n = 0 ; n < driverBurnOkList.size();n++) {
			
			data.add((byte) (driverBurnOkList.get(n) ? 0x01 : 0x00));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		checkBurnOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			driverBurnOkList.add(ProtocolUtil.getUnsignedByte(data.get(index++)) == 1);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.ProgramStateCode;
	}

	public boolean isCheckBurnOk() {
		return checkBurnOk;
	}

	public void setCheckBurnOk(boolean checkBurnOk) {
		this.checkBurnOk = checkBurnOk;
	}

	public List<Boolean> getDriverBurnOkList() {
		return driverBurnOkList;
	}

	public void setDriverBurnOkList(List<Boolean> driverBurnOkList) {
		this.driverBurnOkList = driverBurnOkList;
	}

	@Override
	public String toString() {
		return "Check2ProgramStateData [checkBurnOk=" + checkBurnOk + ", driverBurnOkList=" + driverBurnOkList + "]";
	}
	
	

}
