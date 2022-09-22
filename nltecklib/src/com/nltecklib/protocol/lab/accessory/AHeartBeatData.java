package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;

/**
 * @Description: 与控制板之间的心跳包
 * @Author: JinMei
 * @Date : 2020/01/14 09:29:40
 */
public class AHeartBeatData extends Data implements Queryable, Responsable {

    @Override
    public void encode() {
	// TODO Auto-generated method stub

    }

    @Override
    public void decode(List<Byte> encodeData) {
	// TODO Auto-generated method stub

    }

    @Override
    public Code getCode() {

	return AccessoryCode.HeartBeatCode;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean supportMain() {
	// TODO Auto-generated method stub
	return false;
    }

}
