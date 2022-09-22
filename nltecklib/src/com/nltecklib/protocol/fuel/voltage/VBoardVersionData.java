package com.nltecklib.protocol.fuel.voltage;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.UpgradeSign;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 升级信息查询与设置0x32，支持读写
 * 
 * @author caichao_tang
 *
 */
public class VBoardVersionData extends Data implements BoardNoSupportable, Queryable, Configable, Responsable {
    private byte year;
    private byte month;
    private byte day;
    private int programSize;
    private int packageNum;
    private String version = "";
    private UpgradeSign upgradeSign = UpgradeSign.DO_NOT;
    private String boardType = "";

    public byte getYear() {
	return year;
    }

    public void setYear(byte year) {
	this.year = year;
    }

    public byte getMonth() {
	return month;
    }

    public void setMonth(byte month) {
	this.month = month;
    }

    public byte getDay() {
	return day;
    }

    public void setDay(byte day) {
	this.day = day;
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

    public UpgradeSign getUpgradeSign() {
	return upgradeSign;
    }

    public void setUpgradeSign(UpgradeSign upgradeSign) {
	this.upgradeSign = upgradeSign;
    }

    public String getBoardType() {
	return boardType;
    }

    public void setBoardType(String boardType) {
	this.boardType = boardType;
    }

    @Override
    public void encode() {
	data.add(year);
	data.add(month);
	data.add(day);
	data.addAll(Arrays.asList(ProtocolUtil.split(programSize, 3, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(packageNum, 2, true)));
	String[] versionStrings = version.split("\\.");
	data.add(Byte.parseByte(versionStrings[0]));
	data.add(Byte.parseByte(versionStrings[1]));
	data.add(Byte.parseByte(versionStrings[2]));
	data.add((byte) (upgradeSign == UpgradeSign.DO_NOT ? 0 : 1));
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
	year = data.get(index++);
	month = data.get(index++);
	day = data.get(index++);
	programSize = (int) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true);
	packageNum = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	int a = data.get(index++);
	int b = data.get(index++);
	int c = data.get(index++);
	version = a + "." + b + "." + c;
	upgradeSign = data.get(index++) == 0 ? UpgradeSign.DO_NOT : UpgradeSign.BE_READY;
	StringBuffer stringBuffer = new StringBuffer();
	for (Byte byteData : data.subList(index, data.size())) {
	    stringBuffer.append((char) Integer.parseInt(byteData.toString()));
	}
	boardType = stringBuffer.toString();
    }

    @Override
    public Code getCode() {
	return VolCode.VERSION_INFO;
    }

    @Override
    public String toString() {
	return "VBoardVersionData [year=" + year + ", month=" + month + ", day=" + day + ", programSize=" + programSize + ", packageNum=" + packageNum + ", version=" + version + ", upgradeSign=" + upgradeSign + ", boardType=" + boardType + "]";
    }

}
