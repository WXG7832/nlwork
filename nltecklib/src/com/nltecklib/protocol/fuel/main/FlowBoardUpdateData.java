package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流量板升级数据写入0x12
 * 
 * @author caichao_tang
 *
 */
public class FlowBoardUpdateData extends Data implements Configable,Queryable, Responsable {

	private int packageNum;// 总包数
	private int packageIndex;// 当前包，从0开始
	// 包字节数
	private Date date = new Date();// 更新时间，年月日
	private String version = "";// 版本
	private List<Byte> packageData = new ArrayList<>();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(int packageNum) {
		this.packageNum = packageNum;
	}

	public int getPackageIndex() {
		return packageIndex;
	}

	public void setPackageIndex(int packageIndex) {
		this.packageIndex = packageIndex;
	}

	public int getPackageSize() {
		return packageData.size();
	}

	public List<Byte> getPackageData() {
		return packageData;
	}

	public void setPackageData(List<Byte> packageData) {
		this.packageData = packageData;
	}

	@Override
	public void encode() {
		data.addAll(Arrays.asList(ProtocolUtil.split(packageNum, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageIndex, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageData.size(), 2, true)));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		data.add((byte) year);
		data.add((byte) month);
		data.add((byte) day);
		String[] versionStrings = version.split("\\.");
		data.add(Byte.parseByte(versionStrings[0]));
		data.add(Byte.parseByte(versionStrings[1]));
		data.add(Byte.parseByte(versionStrings[2]));
		data.addAll(packageData);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		packageNum = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		packageIndex = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		int packageSize = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		// 时间
		Calendar cal = Calendar.getInstance();
		int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
		int day = ProtocolUtil.getUnsignedByte(data.get(index++));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		date = (cal.getTime());
		int a = data.get(index++);
		int b = data.get(index++);
		int c = data.get(index++);
		version = a + "." + b + "." + c;
		packageData = data.subList(index, index += packageSize);
	}

	@Override
	public Code getCode() {
		return MainCode.FLOW_UPDATE_CODE;
	}

	@Override
	public String toString() {
		return "FBoardUpdateData [packageNum=" + packageNum + ", packageIndex=" + packageIndex + ", date=" + date
				+ ", version=" + version + ", packageData=" + packageData + "]";
	}

}
