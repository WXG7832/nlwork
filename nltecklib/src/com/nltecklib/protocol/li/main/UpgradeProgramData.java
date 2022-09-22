package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
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
 * 主控升级协议
 * 
 * @author Administrator
 *
 */
public class UpgradeProgramData extends Data implements Configable, Responsable {

	private UpgradeType upgradeType = UpgradeType.Core;
	private String path; // 程序内部升级文件路径

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) upgradeType.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long) path.length(), 2, true)));
		data.addAll(ProtocolUtil.encodeString(path, "US-ASCII", path.length()));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
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
		return MainCode.UpgradeCode;
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

	@Override
	public String toString() {
		return "UpgradeProgramData [upgradeType=" + upgradeType + ", path=" + path + "]";
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

}
