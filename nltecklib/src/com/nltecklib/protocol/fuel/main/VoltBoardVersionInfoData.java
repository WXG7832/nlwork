package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 升级信息查询与设置0x32，支持读写
 * 
 * @author caichao_tang
 *
 */
public class VoltBoardVersionInfoData extends Data implements BoardNoSupportable, Queryable, Configable, Responsable {
	private Date date = new Date();
	private int programSize;
	private int packageNum;
	private String version = "";
	private boolean update;
	private String boardType = "";

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getProgramSize() {
		return programSize;
	}

	public void setProgramSize(int programSize) {
		this.programSize = programSize;
	}

	public int getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(int packageNum) {
		this.packageNum = packageNum;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBoardType() {
		return boardType;
	}

	public void setBoardType(String boardType) {
		this.boardType = boardType;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
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
		data.addAll(Arrays.asList(ProtocolUtil.split(programSize, 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(packageNum, 2, true)));
		String[] versionStrings = version.split("\\.");
		data.add(Byte.parseByte(versionStrings[0]));
		data.add(Byte.parseByte(versionStrings[1]));
		data.add(Byte.parseByte(versionStrings[2]));
		data.add((byte) (update ? 1 : 0));
		List<Byte> boardTypeBytes = ProtocolUtil.convertArrayToList(boardType.getBytes());
		data.addAll(boardTypeBytes);
		for (int i = 0; i < 30 - boardTypeBytes.size(); i++) {
			data.add((byte) 0);
		}
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
		programSize = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
		packageNum = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
		int a = data.get(index++);
		int b = data.get(index++);
		int c = data.get(index++);
		version = a + "." + b + "." + c;
		update = data.get(index++) == 1;
		StringBuffer stringBuffer = new StringBuffer();
		for (Byte byteData : data.subList(index, data.size())) {
			stringBuffer.append((char) Integer.parseInt(byteData.toString()));
		}
		boardType = stringBuffer.toString();
	}

	@Override
	public Code getCode() {
		return MainCode.VOLT_VERSION_INFO_CODE;
	}

	@Override
	public String toString() {
		return "VBoardVersionData [date=" + date + ", programSize=" + programSize + ", packageNum=" + packageNum
				+ ", version=" + version + ", update=" + update + ", boardType=" + boardType + "]";
	}

}
