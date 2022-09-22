package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年1月15日 下午8:01:48
* 查询软件版本号
*/
public class VersionData extends Data implements Queryable, Responsable {
    
	//主版本号，逻辑板或回检板，主控
	private String version;
	//附属的驱动板版本号
	private List<String> subVersions = new ArrayList<>();
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		//软件版本号长度
		data.add((byte) version.length());
        data.addAll(ProtocolUtil.encodeString(version, "utf-8", version.length()));
        
        data.add((byte) subVersions.size());
		for(int n = 0 ; n < subVersions.size() ; n++) {
			
			data.addAll(ProtocolUtil.encodeString(subVersions.get(n), "utf-8", 30));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		version = ProtocolUtil.decodeString(data, index, count, "utf-8");
        index += count;
        count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int n = 0 ; n < count ; n++) {
			
			subVersions.add(ProtocolUtil.decodeString(data, index, 30, "utf-8"));
			index += 30;
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.versionCode;
	}
	
	public void setVersionType(UpgradeType upgradeType) {
		
		 this.chnIndex = upgradeType.ordinal();
	}
	
	public UpgradeType getVersionType() {
		
		return UpgradeType.values()[this.chnIndex];
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getSubVersions() {
		return subVersions;
	}

	public void setSubVersions(List<String> subVersions) {
		this.subVersions = subVersions;
	}
	
	

}
