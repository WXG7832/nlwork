package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月16日 下午2:46:10
* 增强型的程序升级，在原来的基础下提供驱动板标识(两字节)
*/
public class UpgradeProgramExData extends Data implements Configable, Responsable {
   
	private UpgradeType upgradeType = UpgradeType.Core;
	private String path; // 程序内部升级文件路径
	private short  driverFlag;
	
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
		data.addAll(Arrays.asList(ProtocolUtil.split((long) driverFlag, 2, true)));
		data.add((byte) upgradeType.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) path.length(), 2, true)));
		data.addAll(ProtocolUtil.encodeString(path, "US-ASCII", path.length()));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverFlag = (short) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > UpgradeType.values().length - 1) {

			throw new RuntimeException("error upgrade type code :" + code);
		}
		upgradeType = UpgradeType.values()[code];
		// 总字节数
		int count = (int) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		path = ProtocolUtil.decodeString(data, index, count, "US-ASCII");
        index += count;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UpgradeExCode;
	}

	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public short getDriverFlag() {
		return driverFlag;
	}

	public void setDriverFlag(short driverFlag) {
		this.driverFlag = driverFlag;
	}

	@Override
	public String toString() {
		return "UpgradeProgramExData [upgradeType=" + upgradeType + ", path=" + path + ", driverFlag=" + driverFlag
				+ "]";
	}
	
	
	

}
