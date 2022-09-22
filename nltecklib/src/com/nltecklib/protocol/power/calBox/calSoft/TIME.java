package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class TIME extends Data implements Queryable, Configable, Responsable {

    private Date time = new Date();

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

    public Date getTime() {
	return time;
    }

    public void setTime(Date time) {
	this.time = time;
    }

    @Override
    public void encode() {

	data.addAll(ProtocolUtil.encodeDate(time, true));

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;

	time = ProtocolUtil.decodeDate(data.subList(index, index + 6), true);
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.TIME;
    }

    @Override
    public String toString() {
	return "TimeData [time=" + time + "]";
    }

}
