package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class IP extends Data implements Configable, Responsable {

    private String ip = ""; // Ð£×¼Ö÷¿Ø

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

	data.addAll(ProtocolUtil.encodeIp(ip));
    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	ip = ProtocolUtil.decodeIp(data.subList(index, index + 4));
	index += 4;
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.IP;
    }

    public String getIp() {
	return ip;
    }

    public void setIp(String ip) {
	this.ip = ip;
    }

}
