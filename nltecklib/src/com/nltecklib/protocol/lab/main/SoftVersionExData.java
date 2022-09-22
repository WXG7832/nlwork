package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年4月20日 上午10:14:35
* 适用于实验室第2代平台的驱动板内程序版本集合查询
*/
public class SoftVersionExData extends Data implements Queryable, Responsable {
    
	/**
	 * 主控软件版本查询
	 */
	private String coreVersion = "";
	
	private List<String> driverVersion = new ArrayList<>();
	private List<String> pickVersion = new ArrayList<>(); //采集板软件版本	
	private List<String>  moduleVersion = new ArrayList<>(); //内置模片软件版本集合
	
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) coreVersion.length());
		data.addAll(ProtocolUtil.encodeString(coreVersion, "utf-8", coreVersion.length()));
		
		data.add((byte) driverVersion.size());
        for(String version : driverVersion) {
			
        	if(version.length() > 30) {
        		
        		throw new RuntimeException(version + " length is greater than 30");
        	}
        	data.add((byte) version.length());
			data.addAll(ProtocolUtil.encodeString(version, "utf-8", version.length()));
			
		}
        data.add((byte) pickVersion.size());
        for(String version : pickVersion) {
			
        	if(version.length() > 30) {
        		
        		throw new RuntimeException(version + " length is greater than 30");
        	}
        	data.add((byte) version.length());
			data.addAll(ProtocolUtil.encodeString(version, "utf-8", version.length()));
			
		}
		data.add((byte) moduleVersion.size());
		for(String version : moduleVersion) {
			
            if(version.length() > 30) {
        		
        		throw new RuntimeException(version + " length is greater than 30");
        	}
        	data.add((byte) version.length());
			data.addAll(ProtocolUtil.encodeString(version, "utf-8", version.length()));
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		int len = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		coreVersion = ProtocolUtil.decodeString(data,index , len , "utf-8");
		index += len;
		
		//驱动板程序版本
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
        for(int n = 0 ; n < count ; n++) {
			
        	len = ProtocolUtil.getUnsignedByte(data.get(index++));
			String version = ProtocolUtil.decodeString(data,index , len, "utf-8");
			index += len;
			driverVersion.add(version);
		}
		
		//采集板程序版本
        count = ProtocolUtil.getUnsignedByte(data.get(index++));
        for(int n = 0 ; n < count ; n++) {
			
        	len = ProtocolUtil.getUnsignedByte(data.get(index++));
			String version = ProtocolUtil.decodeString(data,index , len, "utf-8");
			index += len;
			pickVersion.add(version);
		}
        
        //膜片程序版本

		count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			len = ProtocolUtil.getUnsignedByte(data.get(index++));
			String version = ProtocolUtil.decodeString(data, index , len, "utf-8");
			index += len;
			moduleVersion.add(version);
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SoftVersionExCode;
	}

	public String getCoreVersion() {
		return coreVersion;
	}

	public void setCoreVersion(String coreVersion) {
		this.coreVersion = coreVersion;
	}

	

	public List<String> getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(List<String> driverVersion) {
		this.driverVersion = driverVersion;
	}

	public List<String> getPickVersion() {
		return pickVersion;
	}

	public void setPickVersion(List<String> pickVersion) {
		this.pickVersion = pickVersion;
	}

	public List<String> getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(List<String> moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	@Override
	public String toString() {
		return "SoftVersionExData [coreVersion=" + coreVersion + ", driverVersion=" + driverVersion + ", pickVersion="
				+ pickVersion + ", moduleVersion=" + moduleVersion + "]";
	}

	
	
	

}
