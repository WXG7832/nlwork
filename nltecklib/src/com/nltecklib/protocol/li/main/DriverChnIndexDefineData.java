package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月26日 上午9:16:59
* 对驱动板内通道顺序的自定义；主控将按定义的顺序对通道序号进行重新定义
*/
public class DriverChnIndexDefineData extends Data implements Configable, Responsable, Queryable {
    
	private boolean enable; //启用开关
	private List<Byte>  chnIndexDefineList = new ArrayList<>();
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		if(Data.getDriverChnCount() != chnIndexDefineList.size()) {
			
			throw new RuntimeException("encode error ,the driver chn count :" + Data.getDriverChnCount() + 
					" != define list size " + chnIndexDefineList.size());
			
		}
		data.add((byte) (enable ? 0x01 : 0x00));
		data.addAll(chnIndexDefineList);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex   =  ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
       
        enable = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
        chnIndexDefineList.addAll(data.subList(index, index + Data.getDriverChnCount()));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.DriverChnIndexDefineCode;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<Byte> getChnIndexDefineList() {
		return chnIndexDefineList;
	}

	public void setChnIndexDefineList(List<Byte> chnIndexDefineList) {
		this.chnIndexDefineList = chnIndexDefineList;
	}
	
	

}
