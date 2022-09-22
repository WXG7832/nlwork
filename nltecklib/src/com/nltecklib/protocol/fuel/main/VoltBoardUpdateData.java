package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 升级数据写入0x33，仅支持写入
 * 
 * @author caichao_tang
 *
 */
public class VoltBoardUpdateData extends Data implements BoardNoSupportable, Configable, Responsable {
	private Date date = new Date();
	private int fileSize;
	private int packageNum;
	private int packageIndex;
	private List<Byte> packageData = new ArrayList<>();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		data.add((byte) year);
		data.add((byte) month);
		data.add((byte) day);
		data.addAll(Arrays.asList(ProtocolUtil.split(fileSize, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageNum, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageIndex, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageData.size(), 2, true)));
		data.addAll(packageData);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		// 时间
		Calendar cal = Calendar.getInstance();
		int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
		int day = ProtocolUtil.getUnsignedByte(data.get(index++));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		date = (cal.getTime());
		fileSize = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
		packageNum = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		packageIndex = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		int packageSize = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		packageData = data.subList(index, index += packageSize);
	}

	@Override
	public Code getCode() {
		return MainCode.VOLT_UPDATE_CODE;
	}

	@Override
	public String toString() {
		return "VoltBoardUpdateData [date=" + date + ", fileSize=" + fileSize + ", packageNum=" + packageNum
				+ ", packageIndex=" + packageIndex + ", packageData=" + packageData + "]";
	}

}
