package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PPickupData.DataPack;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.WorkEnv;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月17日 上午9:56:14
* 类说明
*/
public class PInfoExData extends Data implements Queryable, Responsable {
    
	private String driverVersion = ""; //版本
	private String adVersion = "";    //产品类型
	private List<String> moduleVersions = new ArrayList<>();

	
	@Override
	public boolean supportMain() {
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
		
		data.addAll(ProtocolUtil.encodeString(driverVersion, "utf-8", 30));
		data.addAll(ProtocolUtil.encodeString(adVersion, "utf-8", 30));
		data.add((byte) moduleVersions.size());
		for(int n = 0 ; n < moduleVersions.size() ; n++) {
			
			data.addAll(ProtocolUtil.encodeString(moduleVersions.get(n)
					, "utf-8", 30));
			
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverVersion = ProtocolUtil.decodeString(encodeData, index, index + 30, "utf-8");
		index += 30;
		adVersion = ProtocolUtil.decodeString(encodeData, index, index + 30, "utf-8");
		index += 30;
		int count = data.get(index++);
		for(int n = 0 ; n < count ; n++) {
			
			moduleVersions.add(ProtocolUtil.decodeString(encodeData, index, index+30, "utf-8"));
			index += 30;
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.InfoExCode;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}

	public String getAdVersion() {
		return adVersion;
	}

	public void setAdVersion(String adVersion) {
		this.adVersion = adVersion;
	}

	public List<String> getModuleVersions() {
		return moduleVersions;
	}

	public void setModuleVersions(List<String> moduleVersions) {
		this.moduleVersions = moduleVersions;
	}

	@Override
	public String toString() {
		return "PInfoExData [driverVersion=" + driverVersion + ", adVersion=" + adVersion + ", moduleVersions="
				+ moduleVersions + "]";
	}
	
	

}
