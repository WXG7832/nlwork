package com.nltecklib.protocol.fuel.voltage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 升级数据写入0x33，仅支持写入
 * 
 * @author caichao_tang
 *
 */
public class VBoardVersionWriteData extends Data implements BoardNoSupportable, Configable, Responsable {
    private byte year;
    private byte month;
    private byte day;
    private int fileSize;
    private int packageNum;
    private int packageIndex;
    private int packageSize;
    private List<Byte> packageData = new ArrayList<>();

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
	return packageSize;
    }

    public void setPackageSize(int packageSize) {
	this.packageSize = packageSize;
    }

    public List<Byte> getPackageData() {
	return packageData;
    }

    public void setPackageData(List<Byte> packageData) {
	this.packageData = packageData;
    }

    @Override
    public void encode() {
	data.add(year);
	data.add(month);
	data.add(day);
	data.addAll(Arrays.asList(ProtocolUtil.split(fileSize, 3, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(packageNum, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(packageIndex, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(packageSize, 2, true)));
	data.addAll(packageData);
    }

    @Override
    public void decode(List<Byte> encodeData) {

    }

    @Override
    public Code getCode() {
	return VolCode.VERSION_WRITE;
    }

    @Override
    public String toString() {
	return "VBoardVersionWriteData [year=" + year + ", month=" + month + ", day=" + day + ", fileSize=" + fileSize + ", packageNum=" + packageNum + ", packageIndex=" + packageIndex + ", packageSize=" + packageSize + ", packageData=" + packageData + "]";
    }

}
