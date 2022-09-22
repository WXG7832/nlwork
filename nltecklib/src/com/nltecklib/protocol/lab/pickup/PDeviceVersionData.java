package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 设备版本查询 0x20
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年7月29日 上午9:37:38
 *
 */
public class PDeviceVersionData extends Data implements Queryable, Responsable {

	private String driverVersion = ""; // 驱动板版本
	private String adVersion = ""; // AD板版本
	private String checkVersion = ""; // 回检板版本
	private List<String> moduleVersions = new ArrayList<>();// 模片版本

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.addAll(ProtocolUtil.encodeString(driverVersion, "utf-8", 30));
		data.addAll(ProtocolUtil.encodeString(adVersion, "utf-8", 30));
		data.addAll(ProtocolUtil.encodeString(checkVersion, "utf-8", 30));
		data.add((byte) moduleVersions.size());
		for (int n = 0; n < moduleVersions.size(); n++) {

			data.addAll(ProtocolUtil.encodeString(moduleVersions.get(n), "utf-8", 30));

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
		checkVersion = ProtocolUtil.decodeString(encodeData, index, index + 30, "utf-8");
		index += 30;
		int count = data.get(index++);
		for (int n = 0; n < count; n++) {

			moduleVersions.add(ProtocolUtil.decodeString(encodeData, index, index + 30, "utf-8"));
			index += 30;
		}

	}

	@Override
	public Code getCode() {
		return ChipPickupCode.SoftInfoCode;
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

	public String getCheckVersion() {
		return checkVersion;
	}

	public void setCheckVersion(String checkVersion) {
		this.checkVersion = checkVersion;
	}

	public List<String> getModuleVersions() {
		return moduleVersions;
	}

	public void setModuleVersions(List<String> moduleVersions) {
		this.moduleVersions = moduleVersions;
	}

	@Override
	public String toString() {
		return "PDeviceVersionData [driverVersion=" + driverVersion + ", adVersion=" + adVersion + ", checkVersion="
				+ checkVersion + ", moduleVersions=" + moduleVersions + "]";
	}

}
