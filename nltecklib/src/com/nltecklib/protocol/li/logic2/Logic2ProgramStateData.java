package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年1月5日 下午1:16:19
* 逻辑板及其附带驱动板程序状态协议
*/
public class Logic2ProgramStateData extends Data implements Queryable, Responsable {
   
	private boolean  logicBurnOk;
	private boolean  initOk;
	private List<Boolean>  driverBurnOkList = new ArrayList<>();
	
	
	@Override
	public boolean supportUnit() {

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
		data.add((byte) (logicBurnOk ? 0x01 : 0x00));
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
		logicBurnOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			driverBurnOkList.add(ProtocolUtil.getUnsignedByte(data.get(index++)) == 1);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.ProgramStateCode;
	}

	public boolean isLogicBurnOk() {
		return logicBurnOk;
	}

	public void setLogicBurnOk(boolean logicBurnOk) {
		this.logicBurnOk = logicBurnOk;
	}

	public List<Boolean> getDriverBurnOkList() {
		return driverBurnOkList;
	}

	public void setDriverBurnOkList(List<Boolean> driverBurnOkList) {
		this.driverBurnOkList = driverBurnOkList;
	}
	
	

	public boolean isInitOk() {
		return initOk;
	}

	public void setInitOk(boolean initOk) {
		this.initOk = initOk;
	}

	@Override
	public String toString() {
		return "Logic2ProgramStateData [logicBurnOk=" + logicBurnOk + ", driverBurnOkList=" + driverBurnOkList + "]";
	}
	
	

}
