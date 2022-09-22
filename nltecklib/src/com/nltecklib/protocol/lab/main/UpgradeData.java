package com.nltecklib.protocol.lab.main;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 在线升级
 * @author Administrator
 *
 */
public class UpgradeData extends Data implements Configable,Responsable {

	private UpgradeType upgradeType = UpgradeType.MAIN;
	private int mainVersion; // 主版本号
	private int minorVersion; // 次级版本号
	private int repairVersion; // 修复版本号
	//private int pathlength; // 路径长度
	private String path; //升级路径
	
	
	
	@Override
	public void encode() {
		
		data.add((byte) upgradeType.ordinal());
		data.add((byte) mainVersion);
		data.add((byte) minorVersion);
		data.add((byte) repairVersion);
		//升级文件路径的字节数
		try {
			byte[] pathBytes = path.getBytes("utf-8");
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(pathBytes.length), 2, true)));
			
			for (int i = 0; i < pathBytes.length; i++) {
				
				data.add(pathBytes[i]);				
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > UpgradeType.values().length - 1) {
			
			throw new RuntimeException("error upgrade type code :" + code);
		}
		upgradeType = UpgradeType.values()[code];		
		mainVersion = ProtocolUtil.getUnsignedByte(data.get(index++));
		minorVersion = ProtocolUtil.getUnsignedByte(data.get(index++));
		repairVersion = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int pathLength = (int)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		byte[] pathArray = new byte[pathLength];
		for(int j = index ; j < index + pathLength ; j++) {
			
			pathArray[j - index] = data.get(j);			
		}
		try {
			path = new String(pathArray,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		index += pathLength;	
	}
	
	@Override
	public Code getCode() {
		return MainCode.UpgradeCode;
	}
	
	@Override
	public boolean supportChannel() {
		return true;
	}
	
	@Override
	public boolean supportMain() {
		return true;
	}

	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
	}

	public int getMainVersion() {
		return mainVersion;
	}

	public void setMainVersion(int mainVersion) {
		this.mainVersion = mainVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getRepairVersion() {
		return repairVersion;
	}

	public void setRepairVersion(int repairVersion) {
		this.repairVersion = repairVersion;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "UpgradeData [upgradeType=" + upgradeType + ", mainVersion=" + mainVersion + ", minorVersion="
				+ minorVersion + ", repairVersion=" + repairVersion + ", path=" + path + "]";
	}
	
	
	
	
}
