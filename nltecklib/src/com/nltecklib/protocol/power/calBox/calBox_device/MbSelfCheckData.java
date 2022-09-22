package com.nltecklib.protocol.power.calBox.calBox_device;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年1月22日 下午5:41:50
* 设备自检协议
*/
public class MbSelfCheckData extends Data implements Queryable, Responsable {
   
	//自检数据集合
	private List<DriverCheckData>  checkDataList =  new ArrayList<>();
	
	
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
		
		data.add((byte) checkDataList.size());
		for(int n = 0 ; n < checkDataList.size() ; n++) {
			
			DriverCheckData dcd = checkDataList.get(n);
			dcd.encode();
			data.addAll(dcd.getEncodeData());
			
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			
			DriverCheckData data = new DriverCheckData();
			data.decode(encodeData.subList(index, encodeData.size()));
			index += data.getDataSize();
			
			checkDataList.add(data);
		}
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalBoxDeviceCode.SelfCheckCode;
	}

	public List<DriverCheckData> getCheckDataList() {
		return checkDataList;
	}

	public void setCheckDataList(List<DriverCheckData> checkDataList) {
		this.checkDataList = checkDataList;
	}

	@Override
	public String toString() {
		return "MbSelfCheckData [checkDataList=" + checkDataList + "]";
	}
	
	
	

}
