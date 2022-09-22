package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 回检板软件版本
 * 
 * @author Administrator
 *
 */
public class Check2SoftversionData extends Data implements Queryable, Responsable {

	private int chnCount;
	private String version;

	private List<String> driverVersions = new ArrayList<>();

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
		data.addAll(Arrays.asList(ProtocolUtil.split((long) chnCount, 2, true)));

		data.addAll(ProtocolUtil.encodeString(version, "US-ASCII", 30));
		data.add((byte) driverVersions.size());
		for(int n = 0 ; n < driverVersions.size() ; n++) {
			
			data.addAll(ProtocolUtil.encodeString(driverVersions.get(n), "US-ASCII", 30));
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		// 分区通道数
		chnCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		// 软件版本号
		version = ProtocolUtil.decodeString(data, index, 30, "US-ASCII");
		index += 30;
		
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int n = 0 ; n < count ; n++) {
			
			driverVersions.add(ProtocolUtil.decodeString(data, index, 30, "US-ASCII"));
			index += 30;
		}
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.SoftversionCode;
	}

	public int getChnCount() {
		return chnCount;
	}

	public void setChnCount(int chnCount) {
		this.chnCount = chnCount;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getDriverVersions() {
		return driverVersions;
	}

	public void setDriverVersions(List<String> driverVersions) {
		this.driverVersions = driverVersions;
	}

}
