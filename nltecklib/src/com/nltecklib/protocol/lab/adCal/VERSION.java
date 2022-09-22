package com.nltecklib.protocol.lab.adCal;

import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class VERSION extends Data implements Queryable, Responsable {

    private List<Byte> date = new LinkedList<Byte>(); // 更新日期
    private byte year;
    private byte month;
    private byte day;
    private byte type_len = 0;
    private String type;
    private byte version_len = 0;
    private String version;

    public VERSION() {

    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	date = data.subList(index, index + 3);
	index += 3;
	year = date.get(0);
	month = date.get(1);
	day = date.get(2);
	type_len = (byte) ProtocolUtil.getUnsignedByte(data.get(index));
	index++;
	type = new String(ProtocolUtil.convertListToArray(data.subList(index, index + type_len)));
	index += type_len;
	version_len = (byte) ProtocolUtil.getUnsignedByte(data.get(index));
	index++;
	version = new String(ProtocolUtil.convertListToArray(data.subList(index, index + version_len)));
    }

    @Override
    public Code getCode() {

	return ADCalCode.VERSION;
    }

    public List<Byte> getDate() {
	return date;
    }

    public byte getYear() {
	return year;
    }

    public byte getMonth() {
	return month;
    }

    public byte getDay() {
	return day;
    }

    public byte getTypeLen() {
	return type_len;
    }

    public String getType() {
	return type;
    }

    public byte getVersionLen() {
	return version_len;
    }

    public String getVersion() {
	return version;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "版本(0x0D)";
    }

}
