package com.nltecklib.protocol.lab.adCal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class UPDATE extends Data implements Configable, Responsable {

    private byte year;
    private byte month;
    private byte day;
    private int fileSize; // 文件总字节数
    private short packCount; // 总包数
    private short packIndex; // 当前包序号，从1开始
    private short packSize;
    private List<Byte> packContent = new ArrayList<>();

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public void encode() {
	data.add(year);
	data.add(month);
	data.add(day);
	data.addAll(Arrays.asList(ProtocolUtil.split((long) fileSize, 3, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) packCount, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) packIndex, 2, true)));
	packSize = (short) packContent.size();
	data.addAll(Arrays.asList(ProtocolUtil.split((long) packSize, 2, true)));
	data.addAll(packContent);

    }

    @Override
    public void decode(List<Byte> encodeData) {

	int index = 0;
	data = encodeData;
	year = (byte) ProtocolUtil.getUnsignedByte(data.get(index));
	month = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
	day = (byte) ProtocolUtil.getUnsignedByte(data.get(index++));
	fileSize = (int) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	index += 3;
	packCount = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	packIndex = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	packSize = (short) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	packContent = data.subList(index, index + packSize);
    }

    @Override
    public Code getCode() {

	return ADCalCode.UPDATE;
    }

    public int getFileSize() {
	return fileSize;
    }

    public void setFileSize(int fileSize) {
	this.fileSize = fileSize;
    }

    public int getPackCount() {
	return packCount;
    }

    public void setPackCount(short packCount) {
	this.packCount = packCount;
    }

    public int getPackIndex() {
	return packIndex;
    }

    public void setPackIndex(short packIndex) {
	this.packIndex = packIndex;
    }

    public int getPackSize() {
	return packSize;
    }

    public void setPackSize(short packSize) {
	this.packSize = packSize;
    }

    public List<Byte> getPackContent() {
	return packContent;
    }

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

    public void setPackContent(List<Byte> packContent) {
	this.packContent = packContent;
    }

    public String getDescribe() {
	return "升级(0x0C)";
    }

}
