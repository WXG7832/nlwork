package com.nltecklib.protocol.lab.adCal;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;

public class RESET extends Data implements Configable, Responsable {

    public RESET() {

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

    }

    @Override
    public Code getCode() {

	return ADCalCode.RESET;
    }

    @Override
    public boolean supportChannel() {
	return true;
    }

    public String getDescribe() {
	return "¸´Î»AD²É¼¯°å";
    }

}
