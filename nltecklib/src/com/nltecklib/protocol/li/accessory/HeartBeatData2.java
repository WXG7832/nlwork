package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;

/**
 * 0x28心跳连接
 * 
 * @author caichao_tang
 *
 */
public class HeartBeatData2 extends Data implements Queryable, Responsable {

    @Override
    public boolean supportUnit() {
	return false;
    }

    @Override
    public boolean supportDriver() {
	return true;// 地址号
    }

    @Override
    public boolean supportChannel() {
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
	return AccessoryCode.HEART_BEAT2;
    }

}
